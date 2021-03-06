/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.blockoverlay.v1_18_2

import com.github.sorusclient.client.feature.impl.blockoverlay.BlockOverlay
import com.github.sorusclient.client.util.Color
import v1_18_2.com.mojang.blaze3d.platform.GlStateManager
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem
import v1_18_2.net.minecraft.block.ShapeContext
import v1_18_2.net.minecraft.client.MinecraftClient
import v1_18_2.net.minecraft.client.render.*
import v1_18_2.net.minecraft.client.util.math.MatrixStack
import v1_18_2.net.minecraft.client.util.math.Vector3d
import v1_18_2.net.minecraft.util.hit.BlockHitResult
import v1_18_2.net.minecraft.util.math.Vec3d

@Suppress("UNUSED")
object BlockOverlayHook {

    @JvmStatic
    fun modifyLineWidth(lineWidth: Float): Float {
        if (lineWidth > 1.0f) {
            return BlockOverlay.borderThickness.value.toFloat()
        }
        return lineWidth
    }

    @JvmStatic
    fun render(matrices: MatrixStack, camera: Camera) {
        val vec: Vec3d = camera.pos
        val cam = Vector3d(vec.getX(), vec.getY(), vec.getZ())
        if (MinecraftClient.getInstance().crosshairTarget is BlockHitResult) {
            val blockPos = (MinecraftClient.getInstance().crosshairTarget as BlockHitResult?)!!.blockPos

            val offset = Vector3d(blockPos.x - cam.x, blockPos.y - cam.y, blockPos.z - cam.z)
            val tessellator = Tessellator.getInstance()
            val buffer = tessellator.buffer

            RenderSystem.setShader { GameRenderer.getPositionColorShader() }
            RenderSystem.enableBlend()
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO)

            RenderSystem.enableDepthTest()
            RenderSystem.disableCull()
            RenderSystem.depthMask(false)

            val entry = matrices.peek()

            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR)

            val shape = MinecraftClient.getInstance().world!!.getBlockState(blockPos).getOutlineShape(MinecraftClient.getInstance().world, blockPos, ShapeContext.of(MinecraftClient.getInstance().player))

            shape.forEachBox { minX, minY, minZ, maxX, maxY, maxZ ->
                drawBox(entry, buffer, minX.toFloat() + offset.x.toFloat() - 0.0001f, minY.toFloat() + offset.y.toFloat() - 0.0001f, minZ.toFloat() + offset.z.toFloat() - 0.0001f, maxX.toFloat() + offset.x.toFloat() + 0.0001f, maxY.toFloat() + offset.y.toFloat() + 0.0001f, maxZ.toFloat() + offset.z.toFloat() + 0.0001f, BlockOverlay.fillColor.value)
            }

            tessellator.draw()
            RenderSystem.depthMask(true)
            RenderSystem.enableCull()
        }
    }

    private fun drawBox(entry: MatrixStack.Entry, buffer: BufferBuilder, minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float, color: Color) {
        val position = entry.positionMatrix

        buffer.vertex(position, minX, minY, minZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, minX, minY, maxZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, minX, maxY, maxZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, minX, maxY, minZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()

        buffer.vertex(position, maxX, minY, maxZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, maxX, minY, minZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, maxX, maxY, minZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, maxX, maxY, maxZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()

        buffer.vertex(position, maxX, minY, minZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, minX, minY, minZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, minX, maxY, minZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, maxX, maxY, minZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()

        buffer.vertex(position, minX, minY, maxZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, maxX, minY, maxZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, maxX, maxY, maxZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, minX, maxY, maxZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()

        buffer.vertex(position, minX, maxY, maxZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, maxX, maxY, maxZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, maxX, maxY, minZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, minX, maxY, minZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()

        buffer.vertex(position, maxX, minY, maxZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, minX, minY, maxZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, minX, minY, minZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
        buffer.vertex(position, maxX, minY, minZ).color(color.red.toFloat(), color.green.toFloat(), color.blue.toFloat(), color.alpha.toFloat()).next()
    }

    @JvmStatic
    fun modifyOutlineRed(): Float {
        return BlockOverlay.borderColor.value.red.toFloat()
    }

    @JvmStatic
    fun modifyOutlineGreen(): Float {
        return BlockOverlay.borderColor.value.green.toFloat()
    }

    @JvmStatic
    fun modifyOutlineBlue(): Float {
        return BlockOverlay.borderColor.value.blue.toFloat()
    }

    @JvmStatic
    fun modifyOutlineAlpha(): Float {
        return BlockOverlay.borderColor.value.alpha.toFloat()
    }

}