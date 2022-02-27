package com.github.sorusclient.client.hud.impl.bossbar

import com.github.glassmc.loader.api.GlassLoader
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.hud.HUDElement
import com.github.sorusclient.client.util.Color

class BossBar : HUDElement("bossBar") {

    override val width: Double
        get() = 184.0
    override val height: Double
        get() = 18.0

    override val displayName: String
        get() = "BossBar"

    override fun render(x: Double, y: Double, scale: Double) {
        val bossBars = AdapterManager.getAdapter().world.bossBars
        if (bossBars.isEmpty()) return

        val percent = bossBars[0].percentage
        val bossBarRenderer = GlassLoader.getInstance().getInterface(IBossBarRenderer::class.java)

        bossBarRenderer.renderBossBar(x + 1 * scale, y + 11 * scale, scale, percent)
        val renderer = AdapterManager.getAdapter().renderer
        val minecraftFontRenderer = renderer.getFontRenderer("minecraft")!!
        val bossBarName = bossBars[0].name!!
        minecraftFontRenderer.drawString(
            bossBarName,
            x + width / 2 * scale - minecraftFontRenderer.getWidth(bossBarName) / 2 * scale,
            y + 5.5 * scale - minecraftFontRenderer.getHeight() / 2 * scale,
            scale,
            Color.WHITE,
            true
        )
    }

}