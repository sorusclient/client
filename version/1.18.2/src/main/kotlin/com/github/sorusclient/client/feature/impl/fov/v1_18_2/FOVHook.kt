/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.fov.v1_18_2

import com.github.sorusclient.client.feature.impl.fov.FOV
import v1_18_2.net.minecraft.client.MinecraftClient
import v1_18_2.net.minecraft.entity.attribute.EntityAttributes

@Suppress("UNUSED")
object FOVHook {

    @JvmStatic
    fun modifySpeedFov(ignored: Float): Float {
        val player = MinecraftClient.getInstance().player!!

        var speed = 1.0

        if (player.isSprinting) {
            speed += 0.3 * FOV.getSprintingEffect() / 2
        }

        for (statusEffectInstance in player.statusEffects) {
            val statusEffect = statusEffectInstance.effectType

            speed += statusEffect.attributeModifiers[EntityAttributes.GENERIC_MOVEMENT_SPEED]!!.value * (statusEffectInstance.amplifier + 1) * FOV.getPotionEffect() / 2
        }

        return speed.toFloat()
    }

}