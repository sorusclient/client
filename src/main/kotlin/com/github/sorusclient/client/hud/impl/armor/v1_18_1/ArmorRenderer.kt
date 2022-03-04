package com.github.sorusclient.client.hud.impl.armor.v1_18_1

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.adapter.v1_18_1.Util
import com.github.sorusclient.client.hud.impl.armor.IArmorRenderer
import com.github.sorusclient.client.hud.impl.armor.IArmorRenderer.ArmorRenderType
import v1_18_1.com.mojang.blaze3d.systems.RenderSystem
import v1_18_1.net.minecraft.client.MinecraftClient
import v1_18_1.net.minecraft.client.render.*
import v1_18_1.net.minecraft.item.ItemStack

class ArmorRenderer : Listener, IArmorRenderer {

    override fun run() {
        GlassLoader.getInstance().registerInterface(IArmorRenderer::class.java, this)
    }

    override fun render(item: IItem, x: Double, y: Double, scale: Double) {
        var itemStack = item.inner as ItemStack?
        if (itemStack == null) {
            val item = Util.getItemByItemType(item.type)
            if (item != null) {
                itemStack = ItemStack(item)
            }
        }

        MinecraftClient.getInstance().itemRenderer.renderInGuiWithOverrides(itemStack, x.toInt(), y.toInt())
    }

    override fun renderArmorPlateBackground(x: Double, y: Double, scale: Double) {
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()
        RenderSystem.setShaderTexture(0, v1_18_1.net.minecraft.util.Identifier("textures/gui/icons.png"))

        drawTexture(x, y, 16.0, 9.0, 9 * scale, 9 * scale, 9, 9)
    }

    override fun renderArmorPlate(x: Double, y: Double, scale: Double, armorRenderType: ArmorRenderType?) {
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()
        RenderSystem.setShaderTexture(0, v1_18_1.net.minecraft.util.Identifier("textures/gui/icons.png"))

        if (armorRenderType === ArmorRenderType.FULL) {
            drawTexture(x, y, 43.0, 9.0, 9 * scale, 9 * scale, 9, 9)
        } else if (armorRenderType === ArmorRenderType.HALF) {
            drawTexture(x, y, 25.0, 9.0, 9 * scale, 9 * scale, 9, 9)
        }
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