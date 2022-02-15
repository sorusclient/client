package com.github.sorusclient.client.hud.impl.bossbar.v1_8_9

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.hud.impl.bossbar.IBossBarRenderer
import org.lwjgl.opengl.GL11
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.gui.DrawableHelper
import v1_8_9.net.minecraft.util.Identifier

class BossBarRenderer : Listener, IBossBarRenderer {

    override fun run() {
        GlassLoader.getInstance().registerInterface(IBossBarRenderer::class.java, this)
    }

    override fun renderBossBar(x: Double, y: Double, scale: Double, percent: Double) {
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, 0.0)
        GL11.glScaled(scale, scale, 1.0)
        val drawableHelper = DrawableHelper()
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))
        drawableHelper.drawTexture(0, 0, 0, 74, 183, 5)
        drawableHelper.drawTexture(0, 0, 0, 79, (183 * percent).toInt(), 5)
        GL11.glPopMatrix()
    }

}