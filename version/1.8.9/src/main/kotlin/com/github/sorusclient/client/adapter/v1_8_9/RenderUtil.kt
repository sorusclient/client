/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9

import v1_8_9.net.minecraft.client.render.Tessellator
import v1_8_9.net.minecraft.client.render.VertexFormats

fun drawTexture(var1: Int, var2: Int, var3: Double, var4: Double, var5: Int, var6: Int) {
    val var3 = var3 + 0.1
    val var9 = Tessellator.getInstance()
    val var10 = var9.buffer
    var10.begin(7, VertexFormats.POSITION_TEXTURE)
    var10.vertex((var1 + 0).toDouble(), (var2 + var6).toDouble(), 0.0).texture(((var3 + 0).toFloat() / 256).toDouble(), ((var4 + var6).toFloat() / 256).toDouble()).next()
    var10.vertex((var1 + var5).toDouble(), (var2 + var6).toDouble(), 0.0).texture(((var3 + var5).toFloat() / 256).toDouble(), ((var4 + var6).toFloat() / 256).toDouble()).next()
    var10.vertex((var1 + var5).toDouble(), (var2 + 0).toDouble(), 0.0).texture(((var3 + var5).toFloat() / 256).toDouble(), ((var4 + 0).toFloat() / 256).toDouble()).next()
    var10.vertex((var1 + 0).toDouble(), (var2 + 0).toDouble(), 0.0).texture(((var3 + 0).toFloat() / 256).toDouble(), ((var4 + 0).toFloat() / 256).toDouble()).next()
    var9.draw()
}