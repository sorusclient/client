package com.github.sorusclient.client.hud.impl.coordinates

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.IEntity
import com.github.sorusclient.client.adapter.IFontRenderer
import com.github.sorusclient.client.hud.HUDElement
import com.github.sorusclient.client.setting.display.DisplayedSetting.*
import com.github.sorusclient.client.setting.Setting
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
        this.register("showX", Setting(true).also { showX = it })
        this.register("showY", Setting(true).also { showY = it })
        this.register("showZ", Setting(true).also { showZ = it })
        this.register("identifierColor", Setting(Color.WHITE).also {
            identifierColor = it
        })
        this.register("otherColor", Setting(Color.WHITE).also {
            otherColor = it
        })
        this.register("valueColor", Setting(Color.WHITE).also {
            valueColor = it
        })
        this.register("mode", Setting(Mode.BRACKET).also {
            mode = it
        })
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

    override fun addSettings(settings: MutableList<DisplayedSetting>) {
        super.addSettings(settings)
        settings.add(Toggle(showX, "Show X"))
        settings.add(Toggle(showY, "Show Y"))
        settings.add(Toggle(showZ, "Show Z"))
        settings.add(ColorPicker(identifierColor, "Identifier Color"))
        settings.add(ColorPicker(otherColor, "Other Color"))
        settings.add(ColorPicker(valueColor, "Value Color"))
        settings.add(ClickThrough(mode, "Mode"))
    }

    enum class Mode {
        SEMI_COLON, BRACKET
    }

}