/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.armor.v1_18_2

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.adapter.v1_18_2.Util
import com.github.sorusclient.client.adapter.v1_18_2.drawTexture
import com.github.sorusclient.client.bootstrap.Initializer
import com.github.sorusclient.client.hud.impl.armor.IArmorRenderer
import com.github.sorusclient.client.hud.impl.armor.IArmorRenderer.ArmorRenderType
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem
import v1_18_2.net.minecraft.client.MinecraftClient
import v1_18_2.net.minecraft.item.ItemStack

@Suppress("UNUSED")
class ArmorRenderer : IArmorRenderer{

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
        RenderSystem.setShaderTexture(0, v1_18_2.net.minecraft.util.Identifier("textures/gui/icons.png"))

        drawTexture(x, y, 16.0, 9.0, 9 * scale, 9 * scale, 9, 9)
    }

    override fun renderArmorPlate(x: Double, y: Double, scale: Double, armorRenderType: ArmorRenderType?) {
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()
        RenderSystem.setShaderTexture(0, v1_18_2.net.minecraft.util.Identifier("textures/gui/icons.png"))

        if (armorRenderType === ArmorRenderType.FULL) {
            drawTexture(x, y, 43.0, 9.0, 9 * scale, 9 * scale, 9, 9)
        } else if (armorRenderType === ArmorRenderType.HALF) {
            drawTexture(x, y, 25.0, 9.0, 9 * scale, 9 * scale, 9, 9)
        }
    }

}