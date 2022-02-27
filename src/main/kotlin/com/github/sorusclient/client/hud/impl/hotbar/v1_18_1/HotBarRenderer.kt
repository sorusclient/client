package com.github.sorusclient.client.hud.impl.hotbar.v1_18_1

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.hud.impl.hotbar.IHotBarRenderer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11C
import v1_18_1.com.mojang.blaze3d.systems.RenderSystem
import v1_18_1.net.minecraft.client.MinecraftClient
import v1_18_1.net.minecraft.client.render.*
import v1_18_1.net.minecraft.client.util.math.MatrixStack
import v1_18_1.net.minecraft.item.ItemStack

class HotBarRenderer : Listener, IHotBarRenderer {

    override fun run() {
        GlassLoader.getInstance().registerInterface(IHotBarRenderer::class.java, this)
    }

    override fun renderBackground(x: Double, y: Double, scale: Double) {
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()
        RenderSystem.setShaderTexture(0, v1_18_1.net.minecraft.util.Identifier("textures/gui/widgets.png"))
        drawTexture(x, y, 0.0, 0.0, 182 * scale, 22 * scale, 182, 22)
    }

    override fun renderItem(x: Double, y: Double, scale: Double, item: IItem) {
        MinecraftClient.getInstance().itemRenderer.renderInGuiWithOverrides(item.inner as ItemStack, x.toInt(), y.toInt())
        MinecraftClient.getInstance().itemRenderer.renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, item.inner as ItemStack, x.toInt(), y.toInt(), null)
    }

    override fun renderSelectedSlot(x: Double, y: Double, scale: Double) {
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()
        RenderSystem.setShaderTexture(0, v1_18_1.net.minecraft.util.Identifier("textures/gui/widgets.png"))
        drawTexture(x, y, 0.0, 22.0, 24 * scale, 24 * scale, 24, 24)
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