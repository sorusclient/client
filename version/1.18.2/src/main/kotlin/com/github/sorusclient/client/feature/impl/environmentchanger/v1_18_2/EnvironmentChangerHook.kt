package com.github.sorusclient.client.feature.impl.environmentchanger.v1_18_2

import com.github.sorusclient.client.feature.impl.environmentchanger.EnvironmentChanger
import com.github.sorusclient.client.feature.impl.environmentchanger.EnvironmentChanger.Weather
import v1_18_2.net.minecraft.client.MinecraftClient

@Suppress("UNUSED")
object EnvironmentChangerHook {

    @JvmStatic
    fun modifySkyAngle(angle: Float): Float {
        return if (EnvironmentChanger.modifyTime()) {
            MinecraftClient.getInstance().world!!.dimension.getSkyAngle(EnvironmentChanger.getTimeValue())
        } else {
            angle
        }
    }

    @JvmStatic
    fun modifyTime(time: Long): Long {
        return if (EnvironmentChanger.modifyTime()) {
            EnvironmentChanger.getTimeValue()
        } else {
            time
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