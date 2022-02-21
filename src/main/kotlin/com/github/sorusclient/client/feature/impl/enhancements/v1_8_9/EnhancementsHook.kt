package com.github.sorusclient.client.feature.impl.enhancements.v1_8_9

import com.github.sorusclient.client.feature.FeatureManager
import com.github.sorusclient.client.feature.impl.enhancements.Enhancements
import org.lwjgl.opengl.GL11
import v1_8_9.net.minecraft.client.MinecraftClient

object EnhancementsHook {

    @JvmStatic
    fun preRenderFireFirstPerson() {
        val enhancements = FeatureManager.get<Enhancements>()
        if (enhancements.isEnabled()) {
            GL11.glPushMatrix()
            GL11.glTranslated(0.0, -enhancements.getFireHeightValue() * 0.4, 0.0)
        }
    }

    @JvmStatic
    fun postRenderFireFirstPerson() {
        val enhancements = FeatureManager.get<Enhancements>()
        if (enhancements.isEnabled()) {
            GL11.glPopMatrix()
        }
    }

    @JvmStatic
    fun modifyPotionOffset(offset: Int): Int {
        val enhancements = FeatureManager.get<Enhancements>()
        return if (enhancements.isEnabled() && enhancements.isCenteredInventoryValue()) {
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

}