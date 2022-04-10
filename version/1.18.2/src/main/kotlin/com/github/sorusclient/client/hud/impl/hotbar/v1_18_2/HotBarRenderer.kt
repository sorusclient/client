/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.hotbar.v1_18_2

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.adapter.v1_18_2.drawTexture
import com.github.sorusclient.client.bootstrap.Initializer
import com.github.sorusclient.client.hud.impl.hotbar.IHotBarRenderer
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem
import v1_18_2.net.minecraft.client.MinecraftClient
import v1_18_2.net.minecraft.client.render.*
import v1_18_2.net.minecraft.item.ItemStack

@Suppress("UNUSED")
class HotBarRenderer : IHotBarRenderer, Initializer {

    override fun initialize() {
        InterfaceManager.register(this)
    }

    override fun renderBackground(x: Double, y: Double, scale: Double) {
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()

        RenderSystem.setShaderTexture(0, v1_18_2.net.minecraft.util.Identifier("textures/gui/widgets.png"))
        drawTexture(x, y, 0.0, 0.0, 182 * scale, 22 * scale, 182, 22)
    }

    override fun renderItem(x: Double, y: Double, scale: Double, item: IItem) {
        MinecraftClient.getInstance().itemRenderer.renderInGuiWithOverrides(item.inner as ItemStack, x.toInt(), y.toInt())
        MinecraftClient.getInstance().itemRenderer.renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, item.inner as ItemStack, x.toInt(), y.toInt(), null)
    }

    override fun renderSelectedSlot(x: Double, y: Double, scale: Double) {
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()

        RenderSystem.setShaderTexture(0, v1_18_2.net.minecraft.util.Identifier("textures/gui/widgets.png"))
        drawTexture(x, y, 0.0, 22.0, 24 * scale, 24 * scale, 24, 24)
    }

}