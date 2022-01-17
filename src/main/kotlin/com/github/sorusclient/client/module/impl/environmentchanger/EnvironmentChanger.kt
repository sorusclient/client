package com.github.sorusclient.client.module.impl.environmentchanger

import com.github.sorusclient.client.module.ModuleDisableable
import com.github.sorusclient.client.setting.ConfigurableData
import com.github.sorusclient.client.setting.ConfigurableData.*
import com.github.sorusclient.client.setting.Setting

class EnvironmentChanger : ModuleDisableable("environmentChanger") {

    //TODO: Make world lighting reflect time of day
    private var modifyTime: Setting<Boolean>
    private var time: Setting<Long>
    private var modifyWeather: Setting<Boolean>
    private var weather: Setting<Weather>

    init {
        register("modifyTime", Setting(false).also { modifyTime = it })
        register("time", Setting(5000L).also { time = it })
        register("modifyWeather", Setting(false).also { modifyWeather = it })
        register("weather", Setting(Weather.CLEAR).also { weather = it })
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

    override fun addSettings(settings: MutableList<ConfigurableData>) {
        super.addSettings(settings)
        settings.add(Toggle("Modify Time", modifyTime))
        settings.add(ConfigurableData.Dependent(Slider("Time", time, 0.0, 24000.0), modifyTime, true))
        settings.add(Toggle("Modify Weather", modifyWeather))
        settings.add(ConfigurableData.Dependent(ClickThrough("Weather", weather), modifyWeather, true))
    }

    enum class Weather {
        CLEAR, RAIN
    }

}