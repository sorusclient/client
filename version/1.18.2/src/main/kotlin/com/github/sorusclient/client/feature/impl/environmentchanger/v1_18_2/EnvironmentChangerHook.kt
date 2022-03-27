package com.github.sorusclient.client.feature.impl.environmentchanger.v1_18_2

import com.github.sorusclient.client.feature.FeatureManager
import com.github.sorusclient.client.feature.impl.environmentchanger.EnvironmentChanger
import com.github.sorusclient.client.feature.impl.environmentchanger.EnvironmentChanger.Weather
import v1_18_2.net.minecraft.client.MinecraftClient

object EnvironmentChangerHook {

    @JvmStatic
    fun modifySkyAngle(angle: Float): Float {
        val environmentChanger = FeatureManager.get<EnvironmentChanger>()
        return if (environmentChanger.modifyTime()) {
            MinecraftClient.getInstance().world!!.dimension.getSkyAngle(environmentChanger.getTimeValue())
        } else {
            angle
        }
    }

    @JvmStatic
    fun modifyTime(time: Long): Long {
        val environmentChanger = FeatureManager.get<EnvironmentChanger>()
        return if (environmentChanger.modifyTime()) {
            environmentChanger.getTimeValue()
        } else {
            time
        }
    }

    @JvmStatic
    fun modifyRainGradient(angle: Float): Float {
        val environmentChanger = FeatureManager.get<EnvironmentChanger>()
        return if (environmentChanger.modifyWeather()) {
            when (environmentChanger.getWeatherValue()) {
                Weather.CLEAR -> 0.0F
                Weather.RAIN -> 1.0F
            }
        } else angle
    }

}