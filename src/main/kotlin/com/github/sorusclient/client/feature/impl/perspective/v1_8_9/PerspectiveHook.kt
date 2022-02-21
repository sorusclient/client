package com.github.sorusclient.client.feature.impl.perspective.v1_8_9

import com.github.sorusclient.client.feature.FeatureManager
import com.github.sorusclient.client.feature.impl.perspective.Perspective
import v1_8_9.net.minecraft.util.math.MathHelper

object PerspectiveHook {

    private var x = true
    var yaw = 0f
    var prevYaw = 0f
    var pitch = 0f
    var prevPitch = 0f

    @JvmStatic
    fun modifyDelta(delta: Float): Float {
        val perspective = FeatureManager.get<Perspective>()
        val isYaw = x
        x = !x
        return if (perspective.isEnabled() && perspective.isToggled) {
            if (isYaw) {
                val oldYaw = yaw
                yaw = (yaw.toDouble() + delta.toDouble() * 0.15).toFloat()
                prevYaw += yaw - oldYaw
            } else {
                val oldPitch = pitch
                pitch = (pitch.toDouble() - delta.toDouble() * 0.15).toFloat()
                pitch = MathHelper.clamp(pitch, -90.0f, 90.0f)
                prevPitch += pitch - oldPitch
            }
            0F
        } else {
            delta
        }
    }

    @JvmStatic
    fun modifyPitch(pitch: Float): Float {
        val perspective = FeatureManager.get<Perspective>()
        return if (perspective.isEnabled() && perspective.isToggled) {
            PerspectiveHook.pitch
        } else pitch
    }

    @JvmStatic
    fun modifyYaw(yaw: Float): Float {
        val perspective = FeatureManager.get<Perspective>()
        return if (perspective.isEnabled() && perspective.isToggled) {
            PerspectiveHook.yaw
        } else yaw
    }

    @JvmStatic
    fun modifyPrevPitch(prevPitch: Float): Float {
        val perspective = FeatureManager.get<Perspective>()
        return if (perspective.isEnabled() && perspective.isToggled) {
            PerspectiveHook.prevPitch
        } else prevPitch
    }

    @JvmStatic
    fun modifyPrevYaw(prevYaw: Float): Float {
        val perspective = FeatureManager.get<Perspective>()
        return if (perspective.isEnabled() && perspective.isToggled) {
            PerspectiveHook.prevYaw
        } else prevYaw
    }

}