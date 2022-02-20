package com.github.sorusclient.client.feature.impl.environmentchanger

import com.github.sorusclient.client.setting.Category
import com.github.sorusclient.client.setting.SettingConfigure.*
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
        SettingManager.settingsCategory
            .apply {
                put("environmentChanger", HashMap<String, Any>()
                    .apply {
                        put("enabled", Setting(false).also { enabled = it })
                        put("modifyTime", Setting(false).also { modifyTime = it })
                        put("time", Setting(5000L).also { time = it })
                        put("modifyWeather", Setting(false).also { modifyWeather = it })
                        put("weather", Setting(Weather.CLEAR).also { weather = it })
                    })
            }

        SettingManager.mainUICategory
            .apply {
                add(Category("Environment Changer"))
                    .apply {
                        add(Toggle(enabled, "Enabled"))
                        add(Toggle(modifyTime, "Modify Time"))
                        add(Dependent(Slider(time, "Time", 0.0, 24000.0), modifyTime, true))
                        add(Toggle(modifyWeather, "Modify Weather"))
                        add(Dependent(ClickThrough(weather, "Weather"), modifyWeather, true))
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