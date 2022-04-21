/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.hunger.v1_18_2

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.v1_18_2.drawTexture
import com.github.sorusclient.client.bootstrap.Initializer
import com.github.sorusclient.client.hud.impl.hunger.IHungerRenderer
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem
import v1_18_2.net.minecraft.client.render.BufferRenderer
import v1_18_2.net.minecraft.client.render.GameRenderer
import v1_18_2.net.minecraft.client.render.VertexFormat

@Suppress("UNUSED")
class HungerRenderer : IHungerRenderer{

    override fun renderHunger(x: Double, y: Double, scale: Double, heartRenderType: IHungerRenderer.HeartRenderType) {
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()

        RenderSystem.setShaderTexture(0, v1_18_2.net.minecraft.util.Identifier("textures/gui/icons.png"))

        val xLocation = 52
        if (heartRenderType == IHungerRenderer.HeartRenderType.FULL) {
            drawTexture(x, y, xLocation.toDouble(), 27.0, 9 * scale, 9 * scale, 9, 9)
        } else if (heartRenderType == IHungerRenderer.HeartRenderType.HALF) {
            drawTexture(x, y, xLocation.toDouble() + 9, 27.0, 9 * scale, 9 * scale, 9, 9)
        }
    }

    override fun renderHungerBackground(x: Double, y: Double, scale: Double) {
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()

        RenderSystem.setShaderTexture(0, v1_18_2.net.minecraft.util.Identifier("textures/gui/icons.png"))

        drawTexture(x, y, 16.0, 27.0, 9 * scale, 9 * scale, 9, 9)
    }

}