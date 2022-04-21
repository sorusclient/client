/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.hunger.v1_8_9

import com.github.sorusclient.client.adapter.v1_8_9.drawTexture
import com.github.sorusclient.client.hud.impl.hunger.IHungerRenderer
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.util.Identifier

@Suppress("UNUSED")
class HungerRenderer : IHungerRenderer {

    override fun renderHunger(x: Double, y: Double, scale: Double, heartRenderType: IHungerRenderer.HeartRenderType) {
        GlStateManager.pushMatrix()
        GlStateManager.translated(x, y, 0.0)
        GlStateManager.scaled(scale, scale, 1.0)

        GlStateManager.color4f(1f, 1f, 1f, 1f)
        GlStateManager.enableBlend()

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
        GlStateManager.pushMatrix()
        GlStateManager.translated(x, y, 0.0)
        GlStateManager.scaled(scale, scale, 1.0)

        GlStateManager.enableBlend()
        GlStateManager.color4f(1f, 1f, 1f, 1f)

        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))
        drawTexture(0, 0, 16.0, 27.0, 9, 9)

        GlStateManager.popMatrix()
    }

}