package com.github.sorusclient.client.hud.impl.experience.v1_18_2

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.hud.impl.experience.IExperienceRenderer
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem
import v1_18_2.net.minecraft.client.render.*

class ExperienceRenderer : Listener, IExperienceRenderer {

    override fun run() {
        GlassLoader.getInstance().registerInterface(IExperienceRenderer::class.java, this)
    }

    override fun renderExperienceBar(x: Double, y: Double, scale: Double, percent: Double) {
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()
        RenderSystem.setShaderTexture(0, v1_18_2.net.minecraft.util.Identifier("textures/gui/icons.png"))

        drawTexture(x, y, 0.0, 64.0, 183 * scale, 5 * scale, 183, 5)
        drawTexture(x, y, 0.0, 69.0, (183 * percent) * scale, 5 * scale, (183 * percent).toInt(), 5)
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