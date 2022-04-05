/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.itemphysics.v1_8_9

import com.github.sorusclient.client.feature.impl.itemphysics.ItemPhysics
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.entity.ItemEntity
import v1_8_9.net.minecraft.util.math.BlockPos

@Suppress("UNUSED")
object ItemPhysicsHook {

    @JvmStatic
    fun modifyItemBob(bob: Float): Float {
        return if (ItemPhysics.isEnabled()) {
            0.0F
        } else {
            bob
        }
    }

    @JvmStatic
    fun modifyItemRotate(rotate: Float): Float {
        return if (ItemPhysics.isEnabled()) {
            0.0F
        } else {
            rotate
        }
    }

    private val entityStartTimes: MutableMap<Any, Long> = HashMap()

    @JvmStatic
    fun preRenderItem(entity: ItemEntity) {
        if (ItemPhysics.isEnabled()) {
            val block = MinecraftClient.getInstance().world.getBlockAt(BlockPos(entity.x, entity.y, entity.z))
            if (block.translationKey != "tile.snow") {
                GlStateManager.translated(0.0, -0.225, 0.0)
            } else {
                GlStateManager.translated(0.0, -0.11, 0.0)
            }
            GlStateManager.rotatef(90f, 1f, 0f, 0f)
            if (!entity.onGround) {
                val startTime = entityStartTimes.computeIfAbsent(entity) { System.currentTimeMillis() }
                val travelled = System.currentTimeMillis() - startTime
                GlStateManager.rotatef(travelled * entity.velocityX.toFloat() * -2, 0f, 1f, 0f)
                GlStateManager.rotatef(travelled * entity.velocityZ.toFloat() * 2, 1f, 0f, 0f)
            } else {
                entityStartTimes.remove(entity)
            }
        }
    }

}