/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.coordinates

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.IEntity
import com.github.sorusclient.client.adapter.IFontRenderer
import com.github.sorusclient.client.hud.HUDElement
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedSetting
import com.github.sorusclient.client.util.Color
import com.github.sorusclient.client.util.Pair

class Coordinates : HUDElement("coordinates") {

    private val mode: Setting<Mode>

    private val showX: Setting<Boolean>
    private val showY: Setting<Boolean>
    private val showZ: Setting<Boolean>
    private val identifierColor: Setting<Color>
    private val otherColor: Setting<Color>
    private val valueColor: Setting<Color>
    private val modeType: Setting<ModeType>

    private val customText: Setting<MutableList<MutableList<Pair<String, Color>>>>
    private val centered: Setting<Boolean>

    override val width: Double
        get() = 60.0
    override val height: Double
        get() {
            return when (mode.value) {
                Mode.STANDARD -> {
                    val renderer = AdapterManager.getAdapter().renderer
                    val fontRenderer = renderer.getFontRenderer("minecraft")!!

                    val numberShown = (if (showX.value) 1 else 0) + (if (showY.value) 1 else 0) + (if (showZ.value) 1 else 0)
                    fontRenderer.getHeight() * numberShown + 3 * (numberShown + 1)
                }
                Mode.CUSTOM -> {
                    val renderer = AdapterManager.getAdapter().renderer
                    val fontRenderer = renderer.getFontRenderer("minecraft")!!

                    val numberShown = customText.value.size
                    fontRenderer.getHeight() * numberShown + 3 * (numberShown + 1)
                }
            }
        }


    override val displayName: String
    get() = "Coordinates"

    init {
        category.apply {
            data["mode"] = SettingData(Setting(Mode.STANDARD).also { mode = it })

            data["showX"] = SettingData(Setting(true).also { showX = it })
            data["showY"] = SettingData(Setting(true).also { showY = it })
            data["showZ"] = SettingData(Setting(true).also { showZ = it })
            data["identifierColor"] = SettingData(Setting(Color.WHITE).also { identifierColor = it })
            data["otherColor"] = SettingData(Setting(Color.WHITE).also { otherColor = it })
            data["valueColor"] = SettingData(Setting(Color.WHITE).also { valueColor = it })
            data["modeType"] = SettingData(Setting(ModeType.BRACKET).also { modeType = it })

            data["customText"] = SettingData(Setting<MutableList<MutableList<Pair<String, Color>>>>(arrayListOf(arrayListOf(Pair("Hello World", Color.WHITE)))).also { customText = it })
            data["centered"] = SettingData(Setting(true).also { centered = it })
        }

        uiCategory
            .apply {
                add(DisplayedSetting.ClickThrough(mode, "Mode"))

                add(DisplayedSetting.Dependent(DisplayedSetting.Toggle(showX, "Show X"), mode, Mode.STANDARD))
                add(DisplayedSetting.Dependent(DisplayedSetting.Toggle(showY, "Show Y"), mode, Mode.STANDARD))
                add(DisplayedSetting.Dependent(DisplayedSetting.Toggle(showZ, "Show Z"), mode, Mode.STANDARD))
                add(DisplayedSetting.Dependent(DisplayedSetting.ColorPicker(identifierColor, "Identifier Color"), mode, Mode.STANDARD))
                add(DisplayedSetting.Dependent(DisplayedSetting.ColorPicker(otherColor, "Other Color"), mode, Mode.STANDARD))
                add(DisplayedSetting.Dependent(DisplayedSetting.ColorPicker(valueColor, "Value Color"), mode, Mode.STANDARD))
                add(DisplayedSetting.Dependent(DisplayedSetting.ClickThrough(modeType, "Mode Type"), mode, Mode.STANDARD))

                add(DisplayedSetting.Dependent(DisplayedSetting.Toggle(centered, "Centered"), mode, Mode.CUSTOM))
                add(DisplayedSetting.Dependent(DisplayedSetting.CustomTextColor(customText, "Custom Text"), mode, Mode.CUSTOM))
            }
    }

    override fun render(x: Double, y: Double, scale: Double) {
        when (mode.value) {
            Mode.STANDARD -> renderStandard(x, y, scale)
            Mode.CUSTOM -> renderCustom(x, y, scale)
        }
    }

    private fun renderStandard(x: Double, y: Double, scale: Double) {
        val player: IEntity = AdapterManager.getAdapter().player!!
        val renderer = AdapterManager.getAdapter().renderer
        val fontRenderer = renderer.getFontRenderer("minecraft")!!
        renderer.drawRectangle(x, y, width * scale, height * scale, Color.fromRGB(0, 0, 0, 100))
        var textY = y + 3 * scale
        if (showX.value) {
            renderText(
                fontRenderer,
                getText("X", String.format("%.0f", player.x)), x + 3 * scale, textY, scale
            )
            textY += (3 + fontRenderer.getHeight()) * scale
        }
        if (showY.value) {
            renderText(
                fontRenderer,
                getText("Y", String.format("%.0f", player.y)), x + 3 * scale, textY, scale
            )
            textY += (3 + fontRenderer.getHeight()) * scale
        }
        if (showZ.value) {
            renderText(
                fontRenderer,
                getText("Z", String.format("%.0f", player.z)), x + 3 * scale, textY, scale
            )
        }
    }

    private fun getText(identifier: String, value: String): Array<Pair<String, Color>> {
        return when (modeType.value) {
            ModeType.COLON -> arrayOf(
                Pair(identifier, identifierColor.value),
                Pair(": ", otherColor.value),
                Pair(value, valueColor.value)
            )
            ModeType.BRACKET -> arrayOf(
                Pair("[", otherColor.value),
                Pair(identifier, identifierColor.value),
                Pair("] ", otherColor.value),
                Pair(value, valueColor.value)
            )
        }
    }

    private fun renderCustom(x: Double, y: Double, scale: Double) {
        val renderer = AdapterManager.getAdapter().renderer
        val fontRenderer = renderer.getFontRenderer("minecraft")!!

        renderer.drawRectangle(x, y, width * scale, height * scale, Color.fromRGB(0, 0, 0, 100))

        var lineY = 3.0 * scale
        for (line in customText.value) {
            var lineX = when (centered.value) {
                true -> {
                    var lineWidth = 0.0
                    for (element in line) {
                        lineWidth += (fontRenderer.getWidth(applyValues(element.first)) + 1) * scale
                    }
                    width * scale / 2 - lineWidth / 2
                }
                false -> 3.0 * scale
            }

            for (element in line) {
                val string = applyValues(element.first)
                fontRenderer.drawString(string, x + lineX, y + lineY, scale, element.second)
                lineX += (fontRenderer.getWidth(string) + 1) * scale
            }
            lineY += (3 + fontRenderer.getHeight()) * scale
        }
    }

    private fun applyValues(string: String): String {
        val player: IEntity = AdapterManager.getAdapter().player!!
        var string = string
        string = string.replace("\$X", String.format("%.0f", player.x))
        string = string.replace("\$Y", String.format("%.0f", player.y))
        string = string.replace("\$Z", String.format("%.0f", player.z))
        return string
    }

    private fun renderText(
        fontRenderer: IFontRenderer,
        text: Array<Pair<String, Color>>,
        x: Double,
        y: Double,
        scale: Double
    ) {
        var partialX = 0.0
        for (pair in text) {
            fontRenderer.drawString(pair.first, x + partialX, y, scale, pair.second)
            partialX += fontRenderer.getWidth(pair.first) * scale + 1
        }
    }

    enum class Mode {
        CUSTOM, STANDARD
    }

    enum class ModeType {
        COLON, BRACKET
    }
}