/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.health.v1_8_9

import com.github.sorusclient.client.adapter.v1_8_9.drawTexture
import com.github.sorusclient.client.hud.impl.health.IHealthRenderer
import com.github.sorusclient.client.hud.impl.health.IHealthRenderer.BackgroundType
import com.github.sorusclient.client.hud.impl.health.IHealthRenderer.HeartType
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.util.Identifier

@Suppress("UNUSED")
class HealthRenderer : IHealthRenderer {

    override fun renderHeart(x: Double, y: Double, scale: Double, heartType: HeartType, heartRenderType: IHealthRenderer.HeartRenderType) {
        GlStateManager.pushMatrix()
        GlStateManager.translated(x, y, 0.0)
        GlStateManager.scaled(scale, scale, 1.0)

        GlStateManager.color4f(1f, 1f, 1f, 1f)
        GlStateManager.enableBlend()

        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))

        val xLocation = if (heartType == HeartType.HEALTH) 52 else 52 + 12 * 9
        when (heartRenderType) {
            IHealthRenderer.HeartRenderType.FULL -> {
                drawTexture(0, 0, xLocation.toDouble(), 0.0, 9, 9)
            }
            IHealthRenderer.HeartRenderType.HALF_EMPTY -> {
                drawTexture(0, 0, xLocation.toDouble() + 9, 0.0, 9, 9)
            }
            IHealthRenderer.HeartRenderType.HALF_DAMAGE -> {
                drawTexture(0, 0, xLocation.toDouble() + 18, 0.0, 9, 9)
                drawTexture(0, 0, xLocation.toDouble() + 9, 0.0, 9, 9)
            }
            IHealthRenderer.HeartRenderType.DAMAGE -> {
                drawTexture(0, 0, xLocation.toDouble() + 18, 0.0, 9, 9)
            }
            IHealthRenderer.HeartRenderType.DAMAGE_EMPTY -> {
                drawTexture(0, 0, xLocation.toDouble() + 27, 0.0, 9, 9)
            }
            else -> {}
        }

        GlStateManager.popMatrix()
    }

    override fun renderHeartBackground(x: Double, y: Double, scale: Double, backgroundType: BackgroundType?) {
        GlStateManager.pushMatrix()
        GlStateManager.translated(x, y, 0.0)
        GlStateManager.scaled(scale, scale, 1.0)

        GlStateManager.color4f(1f, 1f, 1f, 1f)
        GlStateManager.enableBlend()

        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))

        when (backgroundType) {
            BackgroundType.STANDARD -> drawTexture(0, 0, 16.0, 0.0, 9, 9)
            BackgroundType.FLASHING_OUTLINE -> drawTexture(0, 0, 25.0, 0.0, 9, 9)
            else -> {}
        }

        GlStateManager.popMatrix()
    }

}