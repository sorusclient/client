/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.health.v1_18_2

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.v1_18_2.drawTexture
import com.github.sorusclient.client.bootstrap.Initializer
import com.github.sorusclient.client.hud.impl.health.IHealthRenderer
import com.github.sorusclient.client.hud.impl.health.IHealthRenderer.BackgroundType
import com.github.sorusclient.client.hud.impl.health.IHealthRenderer.HeartType
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem
import v1_18_2.net.minecraft.client.render.*
import v1_18_2.net.minecraft.util.Identifier

@Suppress("UNUSED")
class HealthRenderer : IHealthRenderer, Initializer {

    override fun initialize() {
        InterfaceManager.register(this)
    }

    override fun renderHeart(x: Double, y: Double, scale: Double, heartType: HeartType, heartRenderType: IHealthRenderer.HeartRenderType) {
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()

        RenderSystem.setShaderTexture(0, Identifier("textures/gui/icons.png"))

        val xLocation = if (heartType == HeartType.HEALTH) 52 else 52 + 12 * 9
        when (heartRenderType) {
            IHealthRenderer.HeartRenderType.FULL -> {
                drawTexture(x, y, xLocation.toDouble(), 0.0, 9 * scale, 9 * scale, 9, 9)
            }
            IHealthRenderer.HeartRenderType.HALF_EMPTY -> {
                drawTexture(x, y, xLocation.toDouble() + 9, 0.0, 9 * scale, 9 * scale, 9, 9)
            }
            IHealthRenderer.HeartRenderType.HALF_DAMAGE -> {
                drawTexture(x, y, xLocation.toDouble() + 18, 0.0, 9 * scale, 9 * scale, 9, 9)
                drawTexture(x, y, xLocation.toDouble() + 9, 0.0, 9 * scale, 9 * scale, 9, 9)
            }
            IHealthRenderer.HeartRenderType.DAMAGE -> {
                drawTexture(x, y, xLocation.toDouble() + 18, 0.0, 9 * scale, 9 * scale, 9, 9)
            }
            IHealthRenderer.HeartRenderType.DAMAGE_EMPTY -> {
                drawTexture(x, y, xLocation.toDouble() + 27, 0.0, 9 * scale, 9 * scale, 9, 9)
            }
            else -> {}
        }

    }

    override fun renderHeartBackground(x: Double, y: Double, scale: Double, backgroundType: BackgroundType?) {
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()
        RenderSystem.setShaderTexture(0, Identifier("textures/gui/icons.png"))

        when (backgroundType) {
            BackgroundType.STANDARD -> drawTexture(x, y, 16.0, 0.0, 9 * scale, 9 * scale, 9, 9)
            BackgroundType.FLASHING_OUTLINE -> drawTexture(x, y, 25.0, 0.0, 9 * scale, 9 * scale, 9, 9)
            else -> {}
        }
    }

}