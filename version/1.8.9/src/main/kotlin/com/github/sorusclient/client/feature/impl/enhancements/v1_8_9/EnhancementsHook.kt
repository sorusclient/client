package com.github.sorusclient.client.feature.impl.enhancements.v1_8_9

import com.github.sorusclient.client.feature.FeatureManager
import com.github.sorusclient.client.feature.impl.enhancements.Enhancements
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.org.lwjgl.opengl.Display

object EnhancementsHook {

    @JvmStatic
    fun preRenderFireFirstPerson() {
        val enhancements = FeatureManager.get<Enhancements>()
        GlStateManager.pushMatrix()
        GlStateManager.translated(0.0, -enhancements.getFireHeightValue() * 0.4, 0.0)
    }

    @JvmStatic
    fun postRenderFireFirstPerson() {
        GlStateManager.popMatrix()
    }

    @JvmStatic
    fun modifyPotionOffset(offset: Int): Int {
        val enhancements = FeatureManager.get<com.github.sorusclient.client.feature.impl.enhancements.v1_8_9.Enhancements>()
        return if (enhancements.isCenteredInventoryValue()) {
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
        FeatureManager.get<Enhancements>().saveSettings()
    }

    lateinit var options: Any

    @JvmStatic
    fun onLoad(options: Any) {
        EnhancementsHook.options = options
        FeatureManager.get<Enhancements>().loadSettings()
    }

    @JvmStatic
    fun onStop() {
        Display.destroy()
    }

}