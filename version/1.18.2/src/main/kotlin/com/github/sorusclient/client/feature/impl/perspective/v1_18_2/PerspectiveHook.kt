package com.github.sorusclient.client.feature.impl.perspective.v1_18_2

import com.github.sorusclient.client.feature.FeatureManager
import com.github.sorusclient.client.feature.impl.perspective.Perspective
import v1_18_2.net.minecraft.util.math.MathHelper

object PerspectiveHook {

    private var x = true
    var yaw = 0.0f
    var prevYaw = 0.0f
    var pitch = 0.0f
    var prevPitch = 0.0f

    @JvmStatic
    fun modifyDelta(delta: Double): Double {
        val perspective = FeatureManager.get<Perspective>()
        val isYaw = x
        x = !x
        return if (perspective.isEnabled() && perspective.isToggled) {
            if (isYaw) {
                val oldYaw = yaw
                yaw = (yaw + delta.toFloat() * 0.15f)
                prevYaw += yaw - oldYaw
            } else {
                val oldPitch = pitch
                pitch = (pitch - delta.toFloat() * 0.15f)
                pitch = MathHelper.clamp(pitch, -90.0f, 90.0f)
                prevPitch += pitch - oldPitch
            }
            0.0
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