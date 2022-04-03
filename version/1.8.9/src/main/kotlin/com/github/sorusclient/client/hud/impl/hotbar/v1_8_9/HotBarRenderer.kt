package com.github.sorusclient.client.hud.impl.hotbar.v1_8_9

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.bootstrap.Initializer
import com.github.sorusclient.client.hud.impl.hotbar.IHotBarRenderer
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.gui.DrawableHelper
import v1_8_9.net.minecraft.client.render.GuiLighting
import v1_8_9.net.minecraft.item.ItemStack
import v1_8_9.net.minecraft.util.Identifier

class HotBarRenderer : IHotBarRenderer, Initializer {

    override fun initialize() {
        InterfaceManager.register(this)
    }

    override fun renderBackground(x: Double, y: Double, scale: Double) {
        GlStateManager.pushMatrix()
        GlStateManager.translated(x, y, 0.0)
        GlStateManager.scaled(scale, scale, 1.0)
        GlStateManager.color4f(1f, 1f, 1f, 1f)
        GlStateManager.enableBlend()
        GlStateManager.enableTexture()
        val drawableHelper = DrawableHelper()
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/widgets.png"))
        drawableHelper.drawTexture(0, 0, 0, 0, 182, 22)
        GlStateManager.popMatrix()
    }

    override fun renderItem(x: Double, y: Double, scale: Double, item: IItem) {
        GlStateManager.pushMatrix()
        GlStateManager.translated(x, y, 0.0)
        GlStateManager.scaled(scale, scale, 1.0)
        GlStateManager.color4f(1f, 1f, 1f, 1f)
        GuiLighting.enable()
        MinecraftClient.getInstance().itemRenderer.renderInGuiWithOverrides(item.inner as ItemStack, 0, 0)
        MinecraftClient.getInstance().itemRenderer.renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, item.inner as ItemStack, 0, 0, null)
        GuiLighting.disable()
        GlStateManager.popMatrix()
    }

    override fun renderSelectedSlot(x: Double, y: Double, scale: Double) {
        GlStateManager.pushMatrix()
        GlStateManager.translated(x, y, 0.0)
        GlStateManager.scaled(scale, scale, 1.0)
        GlStateManager.color4f(1f, 1f, 1f, 1f)
        val drawableHelper = DrawableHelper()
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/widgets.png"))
        drawableHelper.drawTexture(0, 0, 0, 22, 24, 24)
        GlStateManager.popMatrix()
    }

}