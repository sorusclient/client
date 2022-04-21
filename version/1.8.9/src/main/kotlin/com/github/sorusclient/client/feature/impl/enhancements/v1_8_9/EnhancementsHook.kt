/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.enhancements.v1_8_9

import com.github.sorusclient.client.feature.impl.enhancements.Enhancements
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.org.lwjgl.opengl.Display

@Suppress("UNUSED")
object EnhancementsHook {

    @JvmStatic
    fun preRenderFireFirstPerson() {
        GlStateManager.pushMatrix()
        GlStateManager.translated(0.0, -Enhancements.getFireHeightValue() * 0.4, 0.0)
    }

    @JvmStatic
    fun postRenderFireFirstPerson() {
        GlStateManager.popMatrix()
    }

    @JvmStatic
    fun modifyPotionOffset(offset: Int): Int {
        return if (com.github.sorusclient.client.feature.impl.enhancements.v1_8_9.Enhancements.isCenteredInventoryValue()) {
            0
        } else offset
    }

    @JvmStatic
    fun modifyPitch(pitch: Float): Float {
        return if (MinecraftClient.getInstance().options.perspective == 2) {
            -pitch
        } else pitch
    }

    @JvmStatic
    fun modifyPrevPitch(prevPitch: Float): Float {
        return if (MinecraftClient.getInstance().options.perspective == 2) {
            -prevPitch
        } else prevPitch
    }

    @JvmStatic
    fun onSave() {
        Enhancements.saveSettings()
    }

    lateinit var options: Any

    @JvmStatic
    fun onLoad(options: Any) {
        EnhancementsHook.options = options
        Enhancements.loadSettings()
    }

    @JvmStatic
    fun onStop() {
        Display.destroy()
    }

    @JvmStatic
    fun modifySpeedFov(speed: Float): Float {
        return if (Enhancements.isDynamicFov()) {
            speed
        } else {
            1f
        }
    }

}