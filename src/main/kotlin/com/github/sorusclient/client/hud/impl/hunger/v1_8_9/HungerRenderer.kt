package com.github.sorusclient.client.hud.impl.hunger.v1_8_9

import com.github.glassmc.loader.GlassLoader
import com.github.glassmc.loader.Listener
import com.github.sorusclient.client.hud.impl.hunger.IHungerRenderer
import org.lwjgl.opengl.GL11
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.gui.DrawableHelper
import v1_8_9.net.minecraft.util.Identifier

class HungerRenderer : Listener, IHungerRenderer {
    override fun run() {
        GlassLoader.getInstance().registerInterface(IHungerRenderer::class.java, this)
    }

    override fun renderHunger(x: Double, y: Double, scale: Double, heartRenderType: IHungerRenderer.HeartRenderType) {
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, 0.0)
        GL11.glScaled(scale, scale, 1.0)
        val drawableHelper = DrawableHelper()
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))
        val xLocation = 52
        if (heartRenderType == IHungerRenderer.HeartRenderType.FULL) {
            drawableHelper.drawTexture(0, 0, xLocation, 27, 9, 9)
        } else if (heartRenderType == IHungerRenderer.HeartRenderType.HALF) {
            drawableHelper.drawTexture(0, 0, xLocation + 9, 27, 9, 9)
        }
        GL11.glPopMatrix()
    }

    override fun renderHungerBackground(x: Double, y: Double, scale: Double) {
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, 0.0)
        GL11.glScaled(scale, scale, 1.0)
        val drawableHelper = DrawableHelper()
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))
        drawableHelper.drawTexture(0, 0, 16, 27, 9, 9)
        GL11.glPopMatrix()
    }
}