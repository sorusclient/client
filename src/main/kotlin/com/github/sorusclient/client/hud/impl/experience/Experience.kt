package com.github.sorusclient.client.hud.impl.experience

import com.github.glassmc.loader.api.GlassLoader
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.IFontRenderer
import com.github.sorusclient.client.hud.HUDElement
import com.github.sorusclient.client.util.Color

class Experience : HUDElement("experience") {

    override val width: Double
        get() = 185.0
    override val height: Double
        get() = 7.0

    override fun render(x: Double, y: Double, scale: Double) {
        val player = AdapterManager.getAdapter().player!!
        val experiencePercent = player.experiencePercentUntilNextLevel
        val experienceRenderer = GlassLoader.getInstance().getInterface(
            IExperienceRenderer::class.java
        )
        experienceRenderer.renderExperienceBar(x + 1 * scale, y + 1 * scale, scale, experiencePercent)
        val renderer = AdapterManager.getAdapter().renderer
        val minecraftFontRenderer = renderer.getFontRenderer("minecraft")!!
        val experienceLevel = player.experienceLevel.toString()
        drawExperienceLevel(
            minecraftFontRenderer,
            experienceLevel,
            x + width / 2 * scale - minecraftFontRenderer.getWidth(experienceLevel) / 2 * scale,
            y - 1 * scale - minecraftFontRenderer.getHeight() / 2 * scale,
            scale
        )
    }

    private fun drawExperienceLevel(
        fontRenderer: IFontRenderer,
        experienceLevel: String,
        x: Double,
        y: Double,
        scale: Double
    ) {
        fontRenderer.drawString(experienceLevel, x - 1 * scale, y, scale, Color.BLACK)
        fontRenderer.drawString(experienceLevel, x + 1 * scale, y, scale, Color.BLACK)
        fontRenderer.drawString(experienceLevel, x, y - 1, scale, Color.BLACK)
        fontRenderer.drawString(experienceLevel, x, y + 1, scale, Color.BLACK)
        fontRenderer.drawString(experienceLevel, x, y, scale, Color.fromRGB(128, 255, 32, 255))
    }

}