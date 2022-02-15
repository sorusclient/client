package com.github.sorusclient.client.hud.impl.hotbar.v1_8_9

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.hud.impl.hotbar.IHotBarRenderer
import org.lwjgl.opengl.GL11
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.gui.DrawableHelper
import v1_8_9.net.minecraft.client.render.GuiLighting
import v1_8_9.net.minecraft.item.ItemStack
import v1_8_9.net.minecraft.util.Identifier

class HotBarRenderer : Listener, IHotBarRenderer {
    override fun run() {
        GlassLoader.getInstance().registerInterface(IHotBarRenderer::class.java, this)
    }

    override fun renderBackground(x: Double, y: Double, scale: Double) {
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, 0.0)
        GL11.glScaled(scale, scale, 1.0)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
        val drawableHelper = DrawableHelper()
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/widgets.png"))
        drawableHelper.drawTexture(0, 0, 0, 0, 182, 22)
        GL11.glPopMatrix()
    }

    override fun renderItem(x: Double, y: Double, scale: Double, item: IItem) {
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, 0.0)
        GL11.glScaled(scale, scale, 1.0)
        GuiLighting.enable()
        MinecraftClient.getInstance().itemRenderer.renderInGuiWithOverrides(item.inner as ItemStack, 0, 0)
        GuiLighting.disable()
        GL11.glPopMatrix()
    }

    override fun renderSelectedSlot(x: Double, y: Double, scale: Double) {
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, 0.0)
        GL11.glScaled(scale, scale, 1.0)
        val drawableHelper = DrawableHelper()
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/widgets.png"))
        drawableHelper.drawTexture(0, 0, 0, 22, 24, 24)
        GL11.glPopMatrix()
    }
}