/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.v1_18_2

import com.github.sorusclient.client.social.SocialManager
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem
import v1_18_2.net.minecraft.client.MinecraftClient
import v1_18_2.net.minecraft.client.render.*
import v1_18_2.net.minecraft.client.texture.NativeImage
import v1_18_2.net.minecraft.client.texture.NativeImageBackedTexture
import v1_18_2.net.minecraft.client.util.math.MatrixStack
import v1_18_2.net.minecraft.entity.Entity
import v1_18_2.net.minecraft.entity.player.PlayerEntity
import v1_18_2.net.minecraft.text.BaseText
import v1_18_2.net.minecraft.text.Text
import v1_18_2.net.minecraft.util.Identifier
import v1_18_2.net.minecraft.util.math.Matrix4f

object SocialHook {

    init {
        val stream = SocialHook::class.java.classLoader.getResourceAsStream("sorus/ui/sorus.png")
        MinecraftClient.getInstance().textureManager.registerTexture(Identifier("sorus/sorus.png"), NativeImageBackedTexture(NativeImage.read(
            stream
        )))
    }

    @JvmStatic
    fun onRenderNameLight(entity: Entity, name: Text, matrices: MatrixStack) {
        if (entity is PlayerEntity && name.string.contains(entity.gameProfile.name) && SocialManager.onlinePlayers.contains(entity.uuid.toString().replace("-", ""))) {
            RenderSystem.enableBlend()
            RenderSystem.enableTexture()
            RenderSystem.setShaderTexture(0, Identifier("sorus/sorus.png"))
            RenderSystem.setShaderColor(1f, 1f, 1f, 0.15f)

            if (entity.isSneaking) {
                RenderSystem.enableDepthTest()
                RenderSystem.depthMask(true)
            }
            drawTexture(-MinecraftClient.getInstance().textRenderer.getWidth(name) / 2.0 - 11.0, -1.0, 0.0, 0.0, 10.0, 10.0, 256, 256, matrices.peek().positionMatrix)
        }
    }

    @JvmStatic
    fun onRenderNameHeavy(entity: Entity, name: Text, matrices: MatrixStack) {
        if (entity is PlayerEntity && name.string.contains(entity.gameProfile.name) && SocialManager.onlinePlayers.contains(entity.uuid.toString().replace("-", ""))) {
            RenderSystem.enableBlend()
            RenderSystem.enableTexture()
            RenderSystem.setShaderTexture(0, Identifier("sorus/sorus.png"))
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f)

            RenderSystem.enableDepthTest()
            RenderSystem.depthMask(true)
            drawTexture(-MinecraftClient.getInstance().textRenderer.getWidth(name) / 2.0 - 11.0, -1.0, 0.0, 0.0, 10.0, 10.0, 256, 256, matrices.peek().positionMatrix)
        }
    }

    private fun drawTexture(var1: Double, var2: Double, textureX: Double, textureY: Double, width: Double, height: Double, textureWidth: Int, textureHeight: Int, matrix4f: Matrix4f) {
        val textureX = textureX + 0.1
        val width = width - 0.1

        RenderSystem.setShader { GameRenderer.getPositionTexShader() }

        val var9 = Tessellator.getInstance()
        val var10 = var9.buffer
        var10.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
        var10.vertex(matrix4f, (var1 + 0).toFloat(), (var2 + height).toFloat(), 0f).texture((textureX + 0).toFloat() / 256, ((textureY + textureHeight).toFloat() / 256)).next()
        var10.vertex(matrix4f, (var1 + width).toFloat(), (var2 + height).toFloat(), 0f).texture(((textureX + textureWidth).toFloat() / 256), ((textureY + textureHeight).toFloat() / 256)).next()
        var10.vertex(matrix4f, (var1 + width).toFloat(), (var2 + 0).toFloat(), 0f).texture(((textureX + textureWidth).toFloat() / 256), ((textureY + 0).toFloat() / 256)).next()
        var10.vertex(matrix4f, (var1 + 0).toFloat(), (var2 + 0).toFloat(), 0f).texture(((textureX + 0).toFloat() / 256), ((textureY + 0).toFloat() / 256)).next()
        var10.end()
        BufferRenderer.draw(var10)
    }

}