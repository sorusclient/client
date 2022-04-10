/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2

import v1_18_2.com.mojang.blaze3d.systems.RenderSystem
import v1_18_2.net.minecraft.client.render.*

fun drawTexture(var1: Double, var2: Double, textureX: Double, textureY: Double, width: Double, height: Double, textureWidth: Int, textureHeight: Int) {
    val textureX = textureX + 0.1
    val width = width - 0.1

    RenderSystem.setShader { GameRenderer.getPositionTexShader() }
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f)

    val var9 = Tessellator.getInstance()
    val var10 = var9.buffer
    var10.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
    var10.vertex((var1 + 0), (var2 + height), 0.0).texture((textureX + 0).toFloat() / 256, ((textureY + textureHeight).toFloat() / 256)).next()
    var10.vertex((var1 + width), (var2 + height), 0.0).texture(((textureX + textureWidth).toFloat() / 256), ((textureY + textureHeight).toFloat() / 256)).next()
    var10.vertex((var1 + width), (var2 + 0), 0.0).texture(((textureX + textureWidth).toFloat() / 256), ((textureY + 0).toFloat() / 256)).next()
    var10.vertex((var1 + 0), (var2 + 0), 0.0).texture(((textureX + 0).toFloat() / 256), ((textureY + 0).toFloat() / 256)).next()
    var10.end()
    BufferRenderer.draw(var10)
}