/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.cps

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Button
import com.github.sorusclient.client.adapter.IEntity
import com.github.sorusclient.client.adapter.IFontRenderer
import com.github.sorusclient.client.adapter.event.MouseEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.hud.HUDElement
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedSetting
import com.github.sorusclient.client.util.Color
import com.github.sorusclient.client.util.Pair

class CPS : HUDElement("cps") {

    private val mode: Setting<Mode>
    private val centered: Setting<Boolean>

    private val identifierColor: Setting<Color>
    private val otherColor: Setting<Color>
    private val valueColor: Setting<Color>
    private val modeType: Setting<ModeType>

    private val customText: Setting<MutableList<MutableList<Pair<String, Color>>>>

    private val primaryClicks = ArrayList<Long>()
    private val secondaryClicks = ArrayList<Long>()

    override val width: Double
        get() = 60.0
    override val height: Double
        get() {
            return when (mode.value) {
                Mode.STANDARD -> {
                    val renderer = AdapterManager.adapter.renderer
                    val fontRenderer = renderer.getFontRenderer("minecraft")!!

                    fontRenderer.getHeight() + 3 * 2
                }
                Mode.CUSTOM -> {
                    val renderer = AdapterManager.adapter.renderer
                    val fontRenderer = renderer.getFontRenderer("minecraft")!!

                    val numberShown = customText.value.size
                    fontRenderer.getHeight() * numberShown + 3 * (numberShown + 1)
                }
            }
        }


    override val displayName: String
        get() = "CPS"

    init {
        category.apply {
            data["mode"] = SettingData(Setting(Mode.STANDARD).also { mode = it })

            data["identifierColor"] = SettingData(Setting(Color.WHITE).also { identifierColor = it })
            data["otherColor"] = SettingData(Setting(Color.WHITE).also { otherColor = it })
            data["valueColor"] = SettingData(Setting(Color.WHITE).also { valueColor = it })
            data["modeType"] = SettingData(Setting(ModeType.BRACKET).also { modeType = it })

            data["customText"] = SettingData(Setting<MutableList<MutableList<Pair<String, Color>>>>(arrayListOf(arrayListOf(Pair("Hello World", Color.WHITE)))).also { customText = it })
            data["centered"] = SettingData(Setting(true).also { centered = it })
        }

        uiCategory
            .apply {
                add(DisplayedSetting.ClickThrough(mode, "CPS"))
                add(DisplayedSetting.Toggle(centered, "Centered"))

                add(DisplayedSetting.Dependent(DisplayedSetting.ColorPicker(identifierColor, "Identifier Color"), mode, Mode.STANDARD))
                add(DisplayedSetting.Dependent(DisplayedSetting.ColorPicker(otherColor, "Other Color"), mode, Mode.STANDARD))
                add(DisplayedSetting.Dependent(DisplayedSetting.ColorPicker(valueColor, "Value Color"), mode, Mode.STANDARD))
                add(DisplayedSetting.Dependent(DisplayedSetting.ClickThrough(modeType, "Mode Type"), mode, Mode.STANDARD))

                add(DisplayedSetting.Dependent(DisplayedSetting.CustomTextColor(customText, "Custom Text"), mode, Mode.CUSTOM))
            }

        EventManager.register { event: MouseEvent ->
            if (event.button == Button.PRIMARY && event.isPressed) {
                primaryClicks.add(System.currentTimeMillis())
            }
        }
    }

    override fun render(x: Double, y: Double, scale: Double) {
        primaryClicks.removeIf {
            System.currentTimeMillis() - it > 2000
        }

        when (mode.value) {
            Mode.STANDARD -> renderStandard(x, y, scale)
            Mode.CUSTOM -> renderCustom(x, y, scale)
        }
    }

    private fun renderStandard(x: Double, y: Double, scale: Double) {
        val renderer = AdapterManager.adapter.renderer
        val fontRenderer = renderer.getFontRenderer("minecraft")!!
        renderer.drawRectangle(x, y, width * scale, height * scale, Color.fromRGB(0, 0, 0, 100))
        val textY = y + 3 * scale

        val text = getText((primaryClicks.size / 2).toString())
        var textString = ""
        for (pair in text) {
            textString += pair.first
        }

        val textX = if (centered.value) { x + width / 2 * scale - fontRenderer.getWidth(textString) / 2 * scale } else { x + 3 * scale }

        renderText(fontRenderer, text, textX, textY, scale)
    }

    private fun getText(value: String): Array<Pair<String, Color>> {
        return when (modeType.value) {
            ModeType.SEMI_COLON -> arrayOf(
                Pair("CPS", identifierColor.value),
                Pair(": ", otherColor.value),
                Pair(value, valueColor.value)
            )
            ModeType.BRACKET -> arrayOf(
                Pair("[", otherColor.value),
                Pair("CPS", identifierColor.value),
                Pair("] ", otherColor.value),
                Pair(value, valueColor.value)
            )
            ModeType.POST -> arrayOf(
                Pair(value, valueColor.value),
                Pair(" cps", identifierColor.value)
            )
        }
    }

    private fun renderCustom(x: Double, y: Double, scale: Double) {
        val renderer = AdapterManager.adapter.renderer
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
        var string = string
        string = string.replace("\$CPS", (primaryClicks.size / 2).toString())
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
        SEMI_COLON, BRACKET, POST
    }
}