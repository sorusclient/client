package com.github.sorusclient.client.feature.impl.environmentchanger.v1_8_9

import com.github.sorusclient.client.feature.impl.environmentchanger.EnvironmentChanger
import com.github.sorusclient.client.feature.impl.environmentchanger.EnvironmentChanger.Weather
import v1_8_9.net.minecraft.client.MinecraftClient

@Suppress("UNUSED")
object EnvironmentChangerHook {

    @JvmStatic
    fun modifySkyAngle(angle: Float): Float {
        return if (EnvironmentChanger.modifyTime()) {
            MinecraftClient.getInstance().world.dimension.getSkyAngle(EnvironmentChanger.getTimeValue(), 0f)
        } else {
            angle
        }
    }

    @JvmStatic
    fun modifyRainGradient(angle: Float): Float {
        return if (EnvironmentChanger.modifyWeather()) {
            when (EnvironmentChanger.getWeatherValue()) {
                Weather.CLEAR -> 0.0F
                Weather.RAIN -> 1.0F
            }
        } else angle
    }

}