package com.github.sorusclient.client.hud.impl.experience.v1_8_9

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.hud.impl.experience.IExperienceRenderer
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.gui.DrawableHelper
import v1_8_9.net.minecraft.util.Identifier

class ExperienceRenderer : Listener, IExperienceRenderer {
    override fun run() {
        GlassLoader.getInstance().registerInterface(IExperienceRenderer::class.java, this)
    }

    override fun renderExperienceBar(x: Double, y: Double, scale: Double, percent: Double) {
        GlStateManager.pushMatrix()
        GlStateManager.translated(x, y, 0.0)
        GlStateManager.scaled(scale, scale, 1.0)
        val drawableHelper = DrawableHelper()
        GlStateManager.color4f(1f, 1f, 1f, 1f)
        GlStateManager.enableTexture()
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))
        drawableHelper.drawTexture(0, 0, 0, 64, 183, 5)
        drawableHelper.drawTexture(0, 0, 0, 69 /* nice */, (183 * percent).toInt(), 5)
        GlStateManager.popMatrix()
    }
}