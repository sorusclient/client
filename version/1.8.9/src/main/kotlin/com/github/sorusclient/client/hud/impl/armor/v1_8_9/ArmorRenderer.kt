package com.github.sorusclient.client.hud.impl.armor.v1_8_9

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.adapter.v1_8_9.Util
import com.github.sorusclient.client.bootstrap.Initializer
import com.github.sorusclient.client.hud.impl.armor.IArmorRenderer
import com.github.sorusclient.client.hud.impl.armor.IArmorRenderer.ArmorRenderType
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.render.Tessellator
import v1_8_9.net.minecraft.client.render.VertexFormats
import v1_8_9.net.minecraft.item.Item
import v1_8_9.net.minecraft.item.ItemStack
import v1_8_9.net.minecraft.util.Identifier

class ArmorRenderer : IArmorRenderer, Initializer {

    override fun initialize() {
        InterfaceManager.register(this)
    }

    override fun render(item: IItem, x: Double, y: Double, scale: Double) {
        var itemStack = item.inner as ItemStack
        if (itemStack == null) {
            val id = Util.getIdByItemType(item.type)
            if (id != -1) {
                itemStack = ItemStack(Item.byRawId(id))
            }
        }

        GlStateManager.pushMatrix()
        GlStateManager.translated(x, y, 0.0)
        GlStateManager.scaled(scale * 18 / 16, scale * 18 / 16, 0.0)
        MinecraftClient.getInstance().itemRenderer.renderItem(itemStack, 0, 0)
        GlStateManager.popMatrix()
    }

    override fun renderArmorPlateBackground(x: Double, y: Double, scale: Double) {
        GlStateManager.pushMatrix()
        GlStateManager.translated(x, y, 0.0)
        GlStateManager.scaled(scale, scale, 1.0)
        GlStateManager.color4f(1f, 1f, 1f, 1f)
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))
        drawTexture(0, 0, 16.0, 9.0, 9, 9)
        GlStateManager.popMatrix()
    }

    override fun renderArmorPlate(x: Double, y: Double, scale: Double, armorRenderType: ArmorRenderType?) {
        GlStateManager.pushMatrix()
        GlStateManager.translated(x, y, 0.0)
        GlStateManager.scaled(scale, scale, 1.0)
        GlStateManager.color4f(1f, 1f, 1f, 1f)
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))
        if (armorRenderType === ArmorRenderType.FULL) {
            drawTexture(0, 0, 43.0, 9.0, 9, 9)
        } else if (armorRenderType === ArmorRenderType.HALF) {
            drawTexture(0, 0, 25.0, 9.0, 9, 9)
        }
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