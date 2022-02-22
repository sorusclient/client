package com.github.sorusclient.client.feature.impl.environmentchanger

import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData

class EnvironmentChanger {

    //TODO: Make world lighting reflect time of day
    private var modifyTime: Setting<Boolean>
    private var time: Setting<Long>
    private var modifyWeather: Setting<Boolean>
    private var weather: Setting<Weather>

    init {
        SettingManager.settingsCategory
            .apply {
                data["environmentChanger"] = CategoryData()
                    .apply {
                        data["modifyTime"] = SettingData(Setting(false).also { modifyTime = it })
                        data["time"] = SettingData(Setting(5000L).also { time = it })
                        data["modifyWeather"] = SettingData(Setting(false).also { modifyWeather = it })
                        data["weather"] = SettingData(Setting(Weather.CLEAR).also { weather = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Environment Changer"))
                    .apply {
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

    enum class Weather {
        CLEAR, RAIN
    }

}