/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.armor

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.GameMode
import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.adapter.IItem.ItemType
import com.github.sorusclient.client.hud.HUDElement
import com.github.sorusclient.client.hud.HUDManager
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedSetting
import com.github.sorusclient.client.util.Color
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

class Armor : HUDElement("armor") {

    //TODO: Add option for reversing image & durability
    private val mode: Setting<Mode>
    private val showHelmet: Setting<Boolean>
    private val showChestplate: Setting<Boolean>
    private val showLeggings: Setting<Boolean>
    private val showBoots: Setting<Boolean>

    init {
        category
            .apply {
                data["mode"] = SettingData(Setting(Mode.TOTAL).also { mode = it })
                data["showHelmet"] = SettingData(Setting(true).also { showHelmet = it })
                data["showChestplate"] = SettingData(Setting(true).also { showChestplate = it })
                data["showLeggings"] = SettingData(Setting(true).also { showLeggings = it })
                data["showBoots"] = SettingData(Setting(true).also { showBoots = it })
            }


        uiCategory
            .apply {
                add(DisplayedSetting.ClickThrough(mode, "Mode"))
                add(DisplayedSetting.Dependent(DisplayedSetting.Toggle(showHelmet, "Show Helmet"), mode, Mode.INDIVIDUAL))
                add(DisplayedSetting.Dependent(DisplayedSetting.Toggle(showChestplate, "Show Chestplate"), mode, Mode.INDIVIDUAL))
                add(DisplayedSetting.Dependent(DisplayedSetting.Toggle(showLeggings, "Show Leggings"), mode, Mode.INDIVIDUAL))
                add(DisplayedSetting.Dependent(DisplayedSetting.Toggle(showBoots, "Show Boots"), mode, Mode.INDIVIDUAL))
            }
    }

    override val displayName: String
        get() = "Armor"

    override fun render(x: Double, y: Double, scale: Double) {
        val player = AdapterManager.adapter.player!!
        val renderer = AdapterManager.adapter.renderer
        val fontRenderer = renderer.getFontRenderer("minecraft")!!
        val armorRenderer = InterfaceManager.get<IArmorRenderer>()
        when (mode.value) {
            Mode.INDIVIDUAL -> {
                renderer.drawRectangle(x, y, width * scale, height * scale, Color.fromRGB(0, 0, 0, 100))
                var textY = y + 3 * scale
                for ((index, armor) in armor.withIndex()) {
                    if (shouldDisplay(armor, index)) {
                        armorRenderer.render(armor, x + 3 * scale, textY, scale)
                        fontRenderer.drawString(
                            String.format("%.0f", armor.remainingDurability),
                            x + 24 * scale,
                            textY + 5 * scale,
                            scale,
                            Color.WHITE
                        )
                        textY += (5 + fontRenderer.getHeight() * 2) * scale
                    }
                }
            }
            Mode.TOTAL -> {
                if ((!(AdapterManager.adapter.gameMode == GameMode.SURVIVAL || AdapterManager.adapter.gameMode == GameMode.ADVENTURE) || player.armorProtection == 0.0) && !HUDManager.isHudEditScreenOpen.get()) return

                val armor = player.armorProtection.toInt()
                var i = 0
                while (i < 10) {
                    val heartX = x + (1 + i * 8) * scale
                    armorRenderer.renderArmorPlateBackground(heartX, y + 1 * scale, scale)
                    if (i * 2 + 1 < armor) {
                        armorRenderer.renderArmorPlate(
                            heartX,
                            y + 1 * scale,
                            scale,
                            IArmorRenderer.ArmorRenderType.FULL
                        )
                    } else if (i * 2 < armor) {
                        armorRenderer.renderArmorPlate(
                            heartX,
                            y + 1 * scale,
                            scale,
                            IArmorRenderer.ArmorRenderType.HALF
                        )
                    } else {
                        armorRenderer.renderArmorPlate(
                            heartX,
                            y + 1 * scale,
                            scale,
                            IArmorRenderer.ArmorRenderType.EMPTY
                        )
                    }
                    i++
                }
            }
        }
    }

    private fun shouldDisplay(iItem: IItem?, index: Int): Boolean {
        if (iItem == null) return false
        when (index) {
            0 -> return showHelmet.value
            1 -> return showChestplate.value
            2 -> return showLeggings.value
            3 -> return showBoots.value
        }
        return false
    }

    private val armor: List<IItem>
        get() {
            val adapter = AdapterManager.adapter
            val editing = HUDManager.isHudEditScreenOpen.get()
            val realArmor = adapter.player!!.armor
            return if (!editing || realArmor.stream().anyMatch { o: IItem? ->
                    Objects.nonNull(
                        o
                    )
                }) {
                realArmor.filterNotNull()
            } else {
                val fakeArmor: MutableList<IItem> = ArrayList()
                fakeArmor.add(FakeArmorItem(188.0, 240.0, ItemType.IRON_HELMET))
                fakeArmor.add(FakeArmorItem(312.0, 350.0, ItemType.DIAMOND_CHESTPLATE))
                fakeArmor.add(FakeArmorItem(98.0, 146.0, ItemType.GOLD_LEGGINGS))
                fakeArmor.add(FakeArmorItem(68.0, 72.0, ItemType.LEATHER_BOOTS))
                fakeArmor
            }
        }
    override val width: Double
        get() = when (mode.value) {
            Mode.INDIVIDUAL -> 60.0
            Mode.TOTAL -> (1 + 8 * 10 + 1 + 1).toDouble()
        }

    override val height: Double
        get() {
            return when (mode.value) {
                Mode.INDIVIDUAL -> {
                    val renderer = AdapterManager.adapter.renderer
                    val fontRenderer = renderer.getFontRenderer("minecraft")!!
                    val index = AtomicInteger(0)
                    val armor = armor.stream().filter { item: IItem? ->
                        val display: Boolean = shouldDisplay(item, index.get())
                        index.set(index.get() + 1)
                        display
                    }.collect(Collectors.toList())
                    if (armor.size == 0) 0.0 else 3 + armor.size * (5 + fontRenderer.getHeight() * 2)
                }
                Mode.TOTAL -> 11.0
            }
        }

    private class FakeArmorItem(
        override val remainingDurability: Double,
        override val maxDurability: Double,
        override val type: ItemType
    ) : IItem {

        override val inner: Any?
            get() = null

        override val count: Int
            get() = 0

    }

    enum class Mode {
        INDIVIDUAL, TOTAL
    }

}