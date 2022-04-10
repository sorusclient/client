/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.util.Axis

abstract class HUDElement(val id: String) {

    var attached: Setting<MutableMap<String, MutableList<AttachType>>>
    private var x: Setting<Double>
    private var y: Setting<Double>
    private var offsetX: Setting<Double>
    private var offsetY: Setting<Double>
    var scale: Setting<Double>
    var internalId: Setting<String>

    val category = CategoryData()
    val uiCategory = DisplayedCategory(displayName)

    abstract val displayName: String

    init {
        category
            .apply {
                data["x"] = SettingData(Setting(0.0).also { x = it })
                data["y"] = SettingData(Setting(0.0).also { y = it })
                data["offsetX"] = SettingData(Setting(-1.0).also { offsetX = it })
                data["offsetY"] = SettingData(Setting(-1.0).also { offsetY = it })
                data["scale"] = SettingData(Setting(1.0).also { scale = it })
                data["internalId"] = SettingData(Setting("$id-${System.nanoTime() % 1000}").also { internalId = it })
                data["attached"] = SettingData(Setting(MutableMap::class.java as Class<MutableMap<String, MutableList<AttachType>>>, HashMap()).also { attached = it })
            }

        uiCategory.parent = HUDManager.hudDisplayedCategory
    }

    private fun getInternalId(): String {
        return internalId.value
    }

    fun updatePosition(attachedElement: HUDElement?, screenDimensions: DoubleArray) {
        this.updatePosition(attachedElement, screenDimensions, ArrayList())
    }

    private fun updatePosition(
        attachedElement: HUDElement?,
        screenDimensions: DoubleArray,
        alreadyUpdated: MutableList<HUDElement>
    ) {
        if (!alreadyUpdated.contains(this)) {
            alreadyUpdated.add(this)
            var attachTypeX: AttachType? = null
            var attachTypeY: AttachType? = null

            for (attachType in attached.value[attachedElement?.getInternalId() ?: "null"]!!) {
                if (attachType.axis === Axis.X) {
                    attachTypeX = attachType
                } else {
                    attachTypeY = attachType
                }
            }
            val attachedX = attachedElement?.getX(screenDimensions[0]) ?: (screenDimensions[0] / 2)
            val attachedWidth = attachedElement?.scaledWidth ?: screenDimensions[0]
            val attachedY = attachedElement?.getY(screenDimensions[1]) ?: (screenDimensions[1] / 2)
            val attachedHeight = attachedElement?.scaledHeight ?: screenDimensions[1]
            var newX = getX(screenDimensions[0])
            if (attachTypeX != null) {
                newX = attachedX + attachTypeX.otherSide * attachedWidth / 2 - attachTypeX.selfSide * scaledWidth / 2
            }
            var newY = getX(screenDimensions[1])
            if (attachTypeY != null) {
                newY = attachedY + attachTypeY.otherSide * attachedHeight / 2 - attachTypeY.selfSide * scaledHeight / 2
            }
            setPosition(
                newX,
                newY,
                screenDimensions
            )
            for (hudId in this.attached.value.keys) {
                val hud = HUDManager.getById(hudId) ?: continue
                hud.updatePosition(this, screenDimensions, alreadyUpdated)
            }
        }
    }

    fun setPosition(x: Double, y: Double, screenDimensions: DoubleArray) {
        if (x > screenDimensions[0] * 2 / 3) {
            offsetX.realValue = 1.0
        } else if (x > screenDimensions[0] * 1 / 3) {
            offsetX.realValue = 0.0
        } else {
            offsetX.realValue = -1.0
        }
        if (y > screenDimensions[1] * 2 / 3) {
            offsetY.realValue = 1.0
        } else if (x > screenDimensions[0] * 1 / 3) {
            offsetY.setValueRaw(0.0)
        } else {
            offsetY.realValue = -1.0
        }
        this.x.realValue = (x + scaledWidth / 2 * offsetX.value) / screenDimensions[0]
        this.y.realValue = (y + scaledHeight / 2 * offsetY.value) / screenDimensions[1]
    }

    fun getX(screenWidth: Double): Double {
        return -offsetX.value * scaledWidth / 2 + x.value * screenWidth
    }

    fun getY(screenHeight: Double): Double {
        return -offsetY.value * scaledHeight / 2 + y.value * screenHeight
    }

    private fun getScale(): Double {
        return scale.value
    }

    fun addAttached(hudElement: HUDElement?, attachType: AttachType) {
        attached.value.computeIfAbsent(
            hudElement?.getInternalId() ?: "null"
        ) { ArrayList() }.add(attachType)
    }

    fun clearStaticAttached(alreadyCleared: MutableList<HUDElement?>) {
        alreadyCleared.add(this)
        for (key in HashMap(attached.value).keys) {
            if (key == "null") {
                attached.value.remove(key)
            }
        }
        for (elementId in attached.value.keys) {
            val hudElement = HUDManager.getById(elementId)
            if (!alreadyCleared.contains(hudElement)) {
                hudElement!!.clearStaticAttached(alreadyCleared)
            }
        }
    }

    fun detach() {
        for (elementId in attached.value.keys) {
            if (elementId != "null") {
                HUDManager.getById(elementId)!!.detachOther(this)
            }
        }
        attached.value.clear()
    }

    private fun detachOther(hud: HUDElement) {
        attached.value.remove(hud.getInternalId())
    }

    fun delete(alreadyDeleted: MutableList<HUDElement?>) {
        alreadyDeleted.add(this)
        HUDManager.remove(this)
        for (elementId in attached.value.keys) {
            val element = HUDManager.getById(elementId)
            if (!alreadyDeleted.contains(element)) {
                element?.delete(alreadyDeleted)
            }
        }
    }

    private fun getAttached(): List<String> {
        return ArrayList(attached.value.keys)
    }

    protected abstract fun render(x: Double, y: Double, scale: Double)
    abstract val width: Double
    abstract val height: Double
    val scaledWidth: Double
        get() = width * getScale()
    val scaledHeight: Double
        get() = height * getScale()

    fun render() {
        val screenDimensions = AdapterManager.adapter.screenDimensions
        this.render(
            getX(screenDimensions[0]) - scaledWidth / 2, getY(screenDimensions[1]) - scaledHeight / 2,
            getScale()
        )
    }

    fun isAttachedTo(other: HUDElement): Boolean {
        return getAttached().contains(other.getInternalId())
    }

}