/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.fov.v1_8_9

import com.github.sorusclient.client.feature.impl.fov.FOV
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.entity.attribute.EntityAttributes
import v1_8_9.net.minecraft.entity.effect.StatusEffect

@Suppress("UNUSED")
object FOVHook {

    @JvmStatic
    fun modifySpeedFov(ignored: Float): Float {
        val player = MinecraftClient.getInstance().player

        var speed = 1.0

        if (player.isSprinting) {
            speed += 0.3 * FOV.getSprintingEffect() / 2
        }

        for (statusEffectInstance in player.statusEffectInstances) {
            val statusEffect = StatusEffect.STATUS_EFFECTS[statusEffectInstance.effectId]

            val movementSpeedModifier = statusEffect.attributeModifiers[EntityAttributes.GENERIC_MOVEMENT_SPEED]
            if (movementSpeedModifier != null) {
                speed += movementSpeedModifier.amount * (statusEffectInstance.amplifier + 1) * FOV.getPotionEffect() / 2
            }
        }

        return speed.toFloat()
    }

}