/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.v1_8_9

import com.github.sorusclient.client.social.SocialManager
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.render.Tessellator
import v1_8_9.net.minecraft.client.render.VertexFormats
import v1_8_9.net.minecraft.client.texture.NativeImageBackedTexture
import v1_8_9.net.minecraft.entity.Entity
import v1_8_9.net.minecraft.entity.player.PlayerEntity
import v1_8_9.net.minecraft.util.Identifier
import javax.imageio.ImageIO

object SocialHook {

    init {
        val stream = SocialHook::class.java.classLoader.getResourceAsStream("sorus/ui/sorus.png")
        MinecraftClient.getInstance().textureManager.loadTexture(Identifier("sorus/sorus.png"), NativeImageBackedTexture(ImageIO.read(
            stream
        )))
    }

    @JvmStatic
    fun onRenderName(entity: Entity, name: String) {
        if (entity is PlayerEntity && name.contains(entity.gameProfile.name) && SocialManager.onlinePlayers.contains(entity.uuid.toString().replace("-", ""))) {
            GlStateManager.disableDepthTest()
            GlStateManager.depthMask(false)
            MinecraftClient.getInstance().textureManager.bindTexture(Identifier("sorus/sorus.png"))
            GlStateManager.color4f(1f, 1f, 1f, 0.15f)
            drawTexture(-MinecraftClient.getInstance().textRenderer.getStringWidth(name) / 2.0 - 11.0, -1.0, 0.0, 0.0, 10.0, 10.0, 256.0, 256.0)
            GlStateManager.enableDepthTest()
            GlStateManager.depthMask(true)
            GlStateManager.color4f(1f, 1f, 1f, 1f)
            drawTexture(-MinecraftClient.getInstance().textRenderer.getStringWidth(name) / 2.0 - 11.0, -1.0, 0.0, 0.0, 10.0, 10.0, 256.0, 256.0)
        }
    }

    private fun drawTexture(var1: Double, var2: Double, var3: Double, var4: Double, var5: Double, var6: Double, textureWidth: Double, textureHeight: Double) {
        val var3 = var3 + 0.1
        val var9 = Tessellator.getInstance()
        val var10 = var9.buffer
        var10.begin(7, VertexFormats.POSITION_TEXTURE)
        var10.vertex((var1 + 0), (var2 + var6), 0.0).texture(((var3 + 0).toFloat() / 256).toDouble(), ((var4 + textureHeight).toFloat() / 256).toDouble()).next()
        var10.vertex((var1 + var5), (var2 + var6), 0.0).texture(((var3 + textureWidth).toFloat() / 256).toDouble(), ((var4 + textureHeight).toFloat() / 256).toDouble()).next()
        var10.vertex((var1 + var5), (var2 + 0), 0.0).texture(((var3 + textureWidth).toFloat() / 256).toDouble(), ((var4 + 0).toFloat() / 256).toDouble()).next()
        var10.vertex((var1 + 0), (var2 + 0), 0.0).texture(((var3 + 0).toFloat() / 256).toDouble(), ((var4 + 0).toFloat() / 256).toDouble()).next()
        var9.draw()
    }

}