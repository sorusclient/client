/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.bossbar.v1_18_2

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.BossBarColor
import com.github.sorusclient.client.adapter.v1_18_2.drawTexture
import com.github.sorusclient.client.bootstrap.Initializer
import com.github.sorusclient.client.hud.impl.bossbar.IBossBarRenderer
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem
import v1_18_2.net.minecraft.client.render.*

@Suppress("UNUSED")
class BossBarRenderer : IBossBarRenderer, Initializer {

    override fun initialize() {
        InterfaceManager.register(this)
    }

    override fun renderBossBar(x: Double, y: Double, scale: Double, percent: Double, color: BossBarColor) {
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()

        RenderSystem.setShaderTexture(0, v1_18_2.net.minecraft.util.Identifier("textures/gui/bars.png"))

        val textureY = color.ordinal * 10.0

        drawTexture(x, y, 0.0, textureY, 183 * scale, 5 * scale, 183, 5)
        drawTexture(x, y, 0.0, textureY + 5.0, (183 * percent) * scale, 5 * scale, (183 * percent).toInt(), 5)
    }

}