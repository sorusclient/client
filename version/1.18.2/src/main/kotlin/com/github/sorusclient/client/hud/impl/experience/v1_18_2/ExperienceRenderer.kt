/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.experience.v1_18_2

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.v1_18_2.drawTexture
import com.github.sorusclient.client.bootstrap.Initializer
import com.github.sorusclient.client.hud.impl.experience.IExperienceRenderer
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem
import v1_18_2.net.minecraft.client.render.*

@Suppress("UNUSED")
class ExperienceRenderer : IExperienceRenderer{

    override fun renderExperienceBar(x: Double, y: Double, scale: Double, percent: Double) {
        RenderSystem.enableBlend()
        RenderSystem.enableTexture()

        RenderSystem.setShaderTexture(0, v1_18_2.net.minecraft.util.Identifier("textures/gui/icons.png"))
        drawTexture(x, y, 0.0, 64.0, 183 * scale, 5 * scale, 183, 5)
        drawTexture(x, y, 0.0, 69.0, (183 * percent) * scale, 5 * scale, (183 * percent).toInt(), 5)
    }

}