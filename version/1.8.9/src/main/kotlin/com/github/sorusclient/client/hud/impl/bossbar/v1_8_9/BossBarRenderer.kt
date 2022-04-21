/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.bossbar.v1_8_9

import com.github.sorusclient.client.adapter.BossBarColor
import com.github.sorusclient.client.hud.impl.bossbar.IBossBarRenderer
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.gui.DrawableHelper
import v1_8_9.net.minecraft.util.Identifier

@Suppress("UNUSED")
class BossBarRenderer : IBossBarRenderer {

    override fun renderBossBar(x: Double, y: Double, scale: Double, percent: Double, color: BossBarColor) {
        GlStateManager.pushMatrix()
        GlStateManager.translated(x, y, 0.0)
        GlStateManager.scaled(scale, scale, 1.0)

        GlStateManager.color4f(1f, 1f, 1f, 1f)

        val drawableHelper = DrawableHelper()
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/icons.png"))
        drawableHelper.drawTexture(0, 0, 0, 74, 183, 5)
        drawableHelper.drawTexture(0, 0, 0, 79, (183 * percent).toInt(), 5)

        GlStateManager.popMatrix()
    }

}