/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.potions.v1_8_9

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.PotionType
import com.github.sorusclient.client.bootstrap.Initializer
import com.github.sorusclient.client.hud.impl.potions.IPotionEffectRenderer
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.gui.DrawableHelper
import v1_8_9.net.minecraft.entity.effect.StatusEffect
import v1_8_9.net.minecraft.util.Identifier

class PotionEffectRenderer : IPotionEffectRenderer, Initializer {

    override fun initialize() {
        InterfaceManager.register(this)
    }

    override fun render(type: PotionType?, x: Double, y: Double, scale: Double) {
        val id = when (type) {
            PotionType.SPEED -> 1
            PotionType.SLOWNESS -> 2
            PotionType.HASTE -> 3
            PotionType.MINING_FATIGUE -> 4
            PotionType.STRENGTH -> 5
            PotionType.INSTANT_HEALTH -> 6
            PotionType.INSTANT_DAMAGE -> 7
            PotionType.JUMP_BOOST -> 8
            PotionType.NAUSEA -> 9
            PotionType.REGENERATION -> 10
            PotionType.RESISTANCE -> 11
            PotionType.FIRE_RESISTANCE -> 12
            PotionType.WATER_BREATHING -> 13
            PotionType.INVISIBILITY -> 14
            PotionType.BLINDNESS -> 15
            PotionType.NIGHT_VISION -> 16
            PotionType.HUNGER -> 17
            PotionType.WEAKNESS -> 18
            PotionType.POISON -> 19
            PotionType.WITHER -> 20
            PotionType.HEALTH_BOOST -> 21
            PotionType.ABSORPTION -> 22
            PotionType.SATURATION -> 22
            else -> -1
        }
        val index = StatusEffect.STATUS_EFFECTS[id].method_2444()
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/container/inventory.png"))
        GlStateManager.enableTexture()
        GlStateManager.enableBlend()
        GlStateManager.color4f(1f, 1f, 1f, 1f)
        GlStateManager.pushMatrix()
        GlStateManager.translated(x, y, 0.0)
        GlStateManager.scaled(scale, scale, 1.0)
        DrawableHelper().drawTexture(0, 0, index % 8 * 18, 198 + index / 8 * 18, 18, 18)
        GlStateManager.popMatrix()
    }

}