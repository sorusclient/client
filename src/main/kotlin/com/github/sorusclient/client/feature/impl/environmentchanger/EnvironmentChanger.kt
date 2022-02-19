package com.github.sorusclient.client.feature.impl.environmentchanger

import com.github.sorusclient.client.setting.DisplayedCategory
import com.github.sorusclient.client.setting.DisplayedSetting.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager

class EnvironmentChanger {

    private var enabled: Setting<Boolean>
    //TODO: Make world lighting reflect time of day
    private var modifyTime: Setting<Boolean>
    private var time: Setting<Long>
    private var modifyWeather: Setting<Boolean>
    private var weather: Setting<Weather>

    init {
        SettingManager.mainCategory
            .apply {
                registerDisplayed(DisplayedCategory("Environment Changer"))
                    .apply {
                        registerDisplayed(Toggle("Enabled", Setting(false).also { enabled = it }))
                        registerDisplayed(Toggle("Modify Time", Setting(false).also { modifyTime = it }))
                        registerDisplayed(Dependent(Slider("Time", Setting(5000L).also { time = it }, 0.0, 24000.0), modifyTime, true))
                        registerDisplayed(Toggle("Modify Weather", Setting(false).also { modifyWeather = it }))
                        registerDisplayed(Dependent(ClickThrough("Weather", Setting(Weather.CLEAR).also { weather = it }), modifyWeather, true))
                    }
            }
    }

    fun modifyTime(): Boolean {
        return modifyTime.value
    }

    fun getTimeValue(): Long {
        return time.value
    }

    fun modifyWeather(): Boolean {
        return modifyWeather.value
    }

    fun getWeatherValue(): Weather {
        return weather.value
    }

    fun isEnabled(): Boolean {
        return enabled.value
    }

    enum class Weather {
        CLEAR, RAIN
    }

}