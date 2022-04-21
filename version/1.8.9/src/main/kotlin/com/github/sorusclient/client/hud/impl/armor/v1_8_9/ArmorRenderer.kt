/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.armor.v1_8_9

import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.adapter.v1_8_9.Util
import com.github.sorusclient.client.adapter.v1_8_9.drawTexture
import com.github.sorusclient.client.hud.impl.armor.IArmorRenderer
import com.github.sorusclient.client.hud.impl.armor.IArmorRenderer.ArmorRenderType
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.item.Item
import v1_8_9.net.minecraft.item.ItemStack
import v1_8_9.net.minecraft.util.Identifier

@Suppress("UNUSED")
class ArmorRenderer : IArmorRenderer {

    override fun render(item: IItem, x: Double, y: Double, scale: Double) {
        var itemStack = item.inner as ItemStack?
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

}