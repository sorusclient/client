package com.github.sorusclient.client.hud.impl.hunger.v1_8_9

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.hud.impl.hunger.IHungerRenderer
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.render.Tessellator
import v1_8_9.net.minecraft.client.render.VertexFormats
import v1_8_9.net.minecraft.util.Identifier

class HungerRenderer : Listener, IHungerRenderer {

    override fun run() {
        GlassLoader.getInstance().registerInterface(IHungerRenderer::class.java, this)
    }

    override fun renderHunger(x: Double, y: Double, scale: Double, heartRenderType: IHungerRenderer.HeartRenderType) {
        GlStateManager.enableBlend()
        GlStateManager.pushMatrix()
        GlStateManager.translated(x, y, 0.0)
        GlStateManager.scaled(scale, scale, 1.0)
        GlStateManager.color4f(1f, 1f, 1f, 1f)
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))
        val xLocation = 52
        if (heartRenderType == IHungerRenderer.HeartRenderType.FULL) {
            drawTexture(0, 0, xLocation.toDouble(), 27.0, 9, 9)
        } else if (heartRenderType == IHungerRenderer.HeartRenderType.HALF) {
            drawTexture(0, 0, xLocation.toDouble() + 9, 27.0, 9, 9)
        }
        GlStateManager.popMatrix()
    }

    override fun renderHungerBackground(x: Double, y: Double, scale: Double) {
        GlStateManager.enableBlend()
        GlStateManager.pushMatrix()
        GlStateManager.translated(x, y, 0.0)
        GlStateManager.scaled(scale, scale, 1.0)
        GlStateManager.color4f(1f, 1f, 1f, 1f)
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))

        drawTexture(0, 0, 16.0, 27.0, 9, 9)

        GlStateManager.popMatrix()
    }

    private fun drawTexture(var1: Int, var2: Int, var3: Double, var4: Double, var5: Int, var6: Int) {
        val var3 = var3 + 0.1
        val var9 = Tessellator.getInstance()
        val var10 = var9.buffer
        var10.begin(7, VertexFormats.POSITION_TEXTURE)
        var10.vertex((var1 + 0).toDouble(), (var2 + var6).toDouble(), 0.0).texture(((var3 + 0).toFloat() / 256).toDouble(), ((var4 + var6).toFloat() / 256).toDouble()).next()
        var10.vertex((var1 + var5).toDouble(), (var2 + var6).toDouble(), 0.0).texture(((var3 + var5).toFloat() / 256).toDouble(), ((var4 + var6).toFloat() / 256).toDouble()).next()
        var10.vertex((var1 + var5).toDouble(), (var2 + 0).toDouble(), 0.0).texture(((var3 + var5).toFloat() / 256).toDouble(), ((var4 + 0).toFloat() / 256).toDouble()).next()
        var10.vertex((var1 + 0).toDouble(), (var2 + 0).toDouble(), 0.0).texture(((var3 + 0).toFloat() / 256).toDouble(), ((var4 + 0).toFloat() / 256).toDouble()).next()
        var9.draw()
    }

}