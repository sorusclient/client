package com.github.sorusclient.client.hud.impl.health.v1_18_1

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.hud.impl.health.IHealthRenderer
import com.github.sorusclient.client.hud.impl.health.IHealthRenderer.BackgroundType
import com.github.sorusclient.client.hud.impl.health.IHealthRenderer.HeartType
import v1_18_1.com.mojang.blaze3d.systems.RenderSystem
import v1_18_1.net.minecraft.client.MinecraftClient
import v1_18_1.net.minecraft.client.render.BufferRenderer
import v1_18_1.net.minecraft.client.render.GameRenderer
import v1_18_1.net.minecraft.client.render.Tessellator
import v1_18_1.net.minecraft.client.render.VertexFormat
import v1_18_1.net.minecraft.client.render.VertexFormats
import v1_18_1.net.minecraft.util.Identifier

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
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()
        RenderSystem.setShaderTexture(0, Identifier("textures/gui/icons.png"))

        val xLocation = if (heartType == HeartType.HEALTH) 52 else 52 + 12 * 9
        when (heartRenderType) {
            IHealthRenderer.HeartRenderType.FULL -> {
                drawTexture(x, y, xLocation.toDouble(), 0.0, 9 * scale, 9 * scale, 9, 9)
            }
            IHealthRenderer.HeartRenderType.HALF_EMPTY -> {
                drawTexture(x, y, xLocation.toDouble() + 9, 0.0, 9 * scale, 9 * scale, 9, 9)
            }
            IHealthRenderer.HeartRenderType.HALF_DAMAGE -> {
                drawTexture(x, y, xLocation.toDouble() + 18, 0.0, 9 * scale, 9 * scale, 9, 9)
                drawTexture(x, y, xLocation.toDouble() + 9, 0.0, 9 * scale, 9 * scale, 9, 9)
            }
            IHealthRenderer.HeartRenderType.DAMAGE -> {
                drawTexture(x, y, xLocation.toDouble() + 18, 0.0, 9 * scale, 9 * scale, 9, 9)
            }
            IHealthRenderer.HeartRenderType.DAMAGE_EMPTY -> {
                drawTexture(x, y, xLocation.toDouble() + 27, 0.0, 9 * scale, 9 * scale, 9, 9)
            }
            else -> {}
        }

    }

    override fun renderHeartBackground(x: Double, y: Double, scale: Double, backgroundType: BackgroundType?) {
        //GL11.glPushMatrix()
        //GL11.glTranslated(x, y, 0.0)
        //GL11.glScaled(scale, scale, 1.0)
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()
        RenderSystem.setShaderTexture(0, Identifier("textures/gui/icons.png"))

        when (backgroundType) {
            BackgroundType.STANDARD -> drawTexture(x, y, 16.0, 0.0, 9 * scale, 9 * scale, 9, 9)
            BackgroundType.FLASHING_OUTLINE -> drawTexture(x, y, 25.0, 0.0, 9 * scale, 9 * scale, 9, 9)
            else -> {}
        }
        //GL11.glPopMatrix()
    }

    private fun drawTexture(var1: Double, var2: Double, textureX: Double, textureY: Double, width: Double, height: Double, textureWidth: Int, textureHeight: Int) {
        val textureX = textureX + 0.1
        val width = width - 0.1

        RenderSystem.setShader { GameRenderer.getPositionTexShader() }

        val var9 = Tessellator.getInstance()
        val var10 = var9.buffer
        var10.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
        var10.vertex((var1 + 0), (var2 + height), 0.0).texture((textureX + 0).toFloat() / 256, ((textureY + textureHeight).toFloat() / 256)).next()
        var10.vertex((var1 + width), (var2 + height), 0.0).texture(((textureX + textureWidth).toFloat() / 256), ((textureY + textureHeight).toFloat() / 256)).next()
        var10.vertex((var1 + width), (var2 + 0), 0.0).texture(((textureX + textureWidth).toFloat() / 256), ((textureY + 0).toFloat() / 256)).next()
        var10.vertex((var1 + 0), (var2 + 0), 0.0).texture(((textureX + 0).toFloat() / 256), ((textureY + 0).toFloat() / 256)).next()
        var10.end()
        BufferRenderer.draw(var10)
    }

}