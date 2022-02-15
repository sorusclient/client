package com.github.sorusclient.client.hud.impl.health.v1_8_9

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.hud.impl.health.IHealthRenderer
import com.github.sorusclient.client.hud.impl.health.IHealthRenderer.BackgroundType
import com.github.sorusclient.client.hud.impl.health.IHealthRenderer.HeartType
import org.lwjgl.opengl.GL11
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.gui.DrawableHelper
import v1_8_9.net.minecraft.util.Identifier

class HealthRenderer : Listener, IHealthRenderer {
    override fun run() {
        GlassLoader.getInstance().registerInterface(IHealthRenderer::class.java, this)
    }

    override fun renderHeart(
        x: Double,
        y: Double,
        scale: Double,
        heartType: HeartType,
        heartRenderType: IHealthRenderer.HeartRenderType
    ) {
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, 0.0)
        GL11.glScaled(scale, scale, 1.0)
        val drawableHelper = DrawableHelper()
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))
        val xLocation = if (heartType == HeartType.HEALTH) 52 else 52 + 12 * 9
        if (heartRenderType == IHealthRenderer.HeartRenderType.FULL) {
            drawableHelper.drawTexture(0, 0, xLocation, 0, 9, 9)
        } else if (heartRenderType == IHealthRenderer.HeartRenderType.HALF_EMPTY) {
            drawableHelper.drawTexture(0, 0, xLocation + 9, 0, 9, 9)
        } else if (heartRenderType == IHealthRenderer.HeartRenderType.HALF_DAMAGE) {
            drawableHelper.drawTexture(0, 0, xLocation + 18, 0, 9, 9)
            drawableHelper.drawTexture(0, 0, xLocation + 9, 0, 9, 9)
        } else if (heartRenderType == IHealthRenderer.HeartRenderType.DAMAGE) {
            drawableHelper.drawTexture(0, 0, xLocation + 18, 0, 9, 9)
        } else if (heartRenderType == IHealthRenderer.HeartRenderType.DAMAGE_EMPTY) {
            drawableHelper.drawTexture(0, 0, xLocation + 27, 0, 9, 9)
        }
        GL11.glPopMatrix()
    }

    override fun renderHeartBackground(x: Double, y: Double, scale: Double, backgroundType: BackgroundType?) {
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, 0.0)
        GL11.glScaled(scale, scale, 1.0)
        val drawableHelper = DrawableHelper()
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))
        when (backgroundType) {
            BackgroundType.STANDARD -> drawableHelper.drawTexture(0, 0, 16, 0, 9, 9)
            BackgroundType.FLASHING_OUTLINE -> drawableHelper.drawTexture(0, 0, 25, 0, 9, 9)
        }
        GL11.glPopMatrix()
    }
}