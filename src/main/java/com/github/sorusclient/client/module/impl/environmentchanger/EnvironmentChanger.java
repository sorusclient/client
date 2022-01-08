package com.github.sorusclient.client.module.impl.environmentchanger;

import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.SettingConfigurableData;

import java.util.List;

public class EnvironmentChanger extends ModuleDisableable {

    private final Setting<Boolean> modifyTime;
    private final Setting<Long> time;
    private final Setting<Boolean> modifyWeather;
    private final Setting<Weather> weather;

    public EnvironmentChanger() {
        super("environmentChanger");

        this.register("modifyTime", this.modifyTime = new Setting<>(false));
        this.register("time", this.time = new Setting<>(5000L));
        this.register("modifyWeather", this.modifyWeather = new Setting<>(false));
        this.register("weather", this.weather = new Setting<>(Weather.CLEAR));
    }

    public boolean modifyTime() {
        return this.modifyTime.getValue();
    }

    public long getTime() {
        return this.time.getValue();
    }

    public boolean modifyWeather() {
        return this.modifyWeather.getValue();
    }

    public Weather getWeather() {
        return this.weather.getValue();
    }

    @Override
    public void addSettings(List<SettingConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new SettingConfigurableData("Modify Time", this.modifyTime, SettingConfigurableData.ConfigurableType.TOGGLE));
        settings.add(new SettingConfigurableData("Time", this.time, SettingConfigurableData.ConfigurableType.SLIDER, 0.0, 24000.0));
        settings.add(new SettingConfigurableData("Modify Weather", this.modifyWeather, SettingConfigurableData.ConfigurableType.TOGGLE));
        settings.add(new SettingConfigurableData("Weather", this.weather, SettingConfigurableData.ConfigurableType.CLICK_THROUGH));
    }

    public enum Weather {
        CLEAR,
        RAIN,
        TEST,
    }

}
