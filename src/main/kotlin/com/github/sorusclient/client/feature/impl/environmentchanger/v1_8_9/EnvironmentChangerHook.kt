package com.github.sorusclient.client.feature.impl.environmentchanger.v1_8_9

import com.github.sorusclient.client.feature.FeatureManager
import com.github.sorusclient.client.module.ModuleManager
import com.github.sorusclient.client.feature.impl.environmentchanger.EnvironmentChanger
import com.github.sorusclient.client.feature.impl.environmentchanger.EnvironmentChanger.Weather
import v1_8_9.net.minecraft.client.MinecraftClient

object EnvironmentChangerHook {

    @JvmStatic
    fun modifySkyAngle(angle: Float): Float {
        val environmentChanger = FeatureManager.get<EnvironmentChanger>()
        return if (environmentChanger.isEnabled() && environmentChanger.modifyTime()) {
            MinecraftClient.getInstance().world.dimension.getSkyAngle(environmentChanger.getTimeValue(), 0f)
        } else {
            angle
        }
    }

    @JvmStatic
    fun modifyRainGradient(angle: Float): Float {
        val environmentChanger = FeatureManager.get<EnvironmentChanger>()
        return if (environmentChanger.isEnabled() && environmentChanger.modifyWeather()) {
            when (environmentChanger.getWeatherValue()) {
                Weather.CLEAR -> 0.0F
                Weather.RAIN -> 1.0F
            }
        } else angle
    }

}