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

    private var showX: Setting<Boolean>
    private var showY: Setting<Boolean>
    private var showZ: Setting<Boolean>
    private var identifierColor: Setting<Color>
    private var otherColor: Setting<Color>
    private var valueColor: Setting<Color>
    private var mode: Setting<Mode>

    override val width: Double
        get() = 60.0
    override val height: Double
        get() = (12 * ((if (showX.value) 1 else 0) + (if (showY.value) 1 else 0) + if (showZ.value) 1 else 0)).toDouble()

    override val displayName: String
        get() = "Coordinates"

    init {
        category.apply {
            data["showX"] = SettingData(Setting(true).also { showX = it })
            data["showY"] = SettingData(Setting(true).also { showY = it })
            data["showZ"] = SettingData(Setting(true).also { showZ = it })
            data["identifierColor"] = SettingData(Setting(Color.WHITE).also { identifierColor = it })
            data["otherColor"] = SettingData(Setting(Color.WHITE).also { otherColor = it })
            data["valueColor"] = SettingData(Setting(Color.WHITE).also { valueColor = it })
            data["mode"] = SettingData(Setting(Mode.BRACKET).also { mode = it })
        }

        uiCategory
            .apply {
                add(DisplayedSetting.Toggle(showX, "Show X"))
                add(DisplayedSetting.Toggle(showY, "Show Y"))
                add(DisplayedSetting.Toggle(showZ, "Show Z"))
                add(DisplayedSetting.ColorPicker(identifierColor, "Identifier Color"))
                add(DisplayedSetting.ColorPicker(otherColor, "Other Color"))
                add(DisplayedSetting.ColorPicker(valueColor, "Value Color"))
                add(DisplayedSetting.ClickThrough(mode, "Mode"))
            }
    }

    override fun render(x: Double, y: Double, scale: Double) {
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
        return when (mode.value) {
            Mode.SEMI_COLON -> arrayOf(
                Pair(identifier, identifierColor.value),
                Pair(": ", otherColor.value),
                Pair(value, valueColor.value)
            )
            Mode.BRACKET -> arrayOf(
                Pair("[", otherColor.value),
                Pair(identifier, identifierColor.value),
                Pair("] ", otherColor.value),
                Pair(value, valueColor.value)
            )
        }
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
        SEMI_COLON, BRACKET
    }

}