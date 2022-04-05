/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.itemphysics.v1_18_2

import com.github.sorusclient.client.feature.impl.itemphysics.ItemPhysics
import v1_18_2.net.minecraft.client.MinecraftClient
import v1_18_2.net.minecraft.client.util.math.MatrixStack
import v1_18_2.net.minecraft.entity.ItemEntity
import v1_18_2.net.minecraft.util.math.BlockPos
import v1_18_2.net.minecraft.util.math.Vec3f

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
    fun preRenderItem(entity: ItemEntity, matrixStack: MatrixStack) {
        if (ItemPhysics.isEnabled()) {
            val block = MinecraftClient.getInstance().world!!.getBlockState(BlockPos(entity.x, entity.y, entity.z))
            if (!block.toString().contains("minecraft:snow")) {
                matrixStack.translate(0.0, 0.0225, 0.0)
            } else {
                matrixStack.translate(0.0, 0.14, 0.0)
            }

            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90f))
            if (!entity.isOnGround) {
                val startTime = entityStartTimes.computeIfAbsent(entity) { System.currentTimeMillis() }
                val travelled = System.currentTimeMillis() - startTime
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(travelled * entity.velocity.x.toFloat() * -2))
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(travelled * entity.velocity.z.toFloat() * 2))
            } else {
                entityStartTimes.remove(entity)
            }

            matrixStack.translate(0.0, -0.25, 0.0)
        }
    }

}