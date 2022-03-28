package com.github.sorusclient.client.feature.impl.itemphysics.v1_8_9

import com.github.sorusclient.client.feature.FeatureManager
import com.github.sorusclient.client.feature.impl.itemphysics.ItemPhysics
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.entity.ItemEntity
import v1_8_9.net.minecraft.util.math.BlockPos

object ItemPhysicsHook {

    @JvmStatic
    fun modifyItemBob(bob: Float): Float {
        val itemPhysics = FeatureManager.get<ItemPhysics>()
        return if (itemPhysics.isEnabled()) {
            0.0F
        } else {
            bob
        }
    }

    @JvmStatic
    fun modifyItemRotate(rotate: Float): Float {
        val itemPhysics = FeatureManager.get<ItemPhysics>()
        return if (itemPhysics.isEnabled()) {
            0.0F
        } else {
            rotate
        }
    }

    private val entityStartTimes: MutableMap<Any, Long> = HashMap()

    @JvmStatic
    fun preRenderItem(entity: ItemEntity) {
        val itemPhysics = FeatureManager.get<ItemPhysics>()
        if (itemPhysics.isEnabled()) {
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