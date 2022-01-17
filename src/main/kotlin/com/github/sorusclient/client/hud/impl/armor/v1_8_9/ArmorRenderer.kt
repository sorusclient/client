package com.github.sorusclient.client.hud.impl.armor.v1_8_9

import com.github.glassmc.loader.GlassLoader
import com.github.glassmc.loader.Listener
import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.adapter.v1_8_9.Util
import com.github.sorusclient.client.hud.impl.armor.IArmorRenderer
import com.github.sorusclient.client.hud.impl.armor.IArmorRenderer.ArmorRenderType
import org.lwjgl.opengl.GL11
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.gui.DrawableHelper
import v1_8_9.net.minecraft.item.Item
import v1_8_9.net.minecraft.item.ItemStack
import v1_8_9.net.minecraft.util.Identifier

class ArmorRenderer : Listener, IArmorRenderer {
    override fun run() {
        GlassLoader.getInstance().registerInterface(IArmorRenderer::class.java, this)
    }

    override fun render(item: IItem, x: Double, y: Double, scale: Double) {
        var itemStack = item.inner as ItemStack
        if (itemStack == null) {
            val id = Util.getIdByItemType(item.type)
            if (id != -1) {
                itemStack = ItemStack(Item.byRawId(id))
            }
        }
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, 0.0)
        GL11.glScaled(scale * 18 / 16, scale * 18 / 16, 0.0)
        MinecraftClient.getInstance().itemRenderer.renderItem(itemStack, 0, 0)
        GL11.glPopMatrix()
    }

    override fun renderArmorPlateBackground(x: Double, y: Double, scale: Double) {
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, 0.0)
        GL11.glScaled(scale, scale, 1.0)
        val drawableHelper = DrawableHelper()
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))
        drawableHelper.drawTexture(0, 0, 16, 9, 9, 9)
        GL11.glPopMatrix()
    }

    override fun renderArmorPlate(x: Double, y: Double, scale: Double, armorRenderType: ArmorRenderType?) {
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, 0.0)
        GL11.glScaled(scale, scale, 1.0)
        val drawableHelper = DrawableHelper()
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))
        if (armorRenderType === ArmorRenderType.FULL) {
            drawableHelper.drawTexture(0, 0, 43, 9, 9, 9)
        } else if (armorRenderType === ArmorRenderType.HALF) {
            drawableHelper.drawTexture(0, 0, 25, 9, 9, 9)
        }
        GL11.glPopMatrix()
    }
}