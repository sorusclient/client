package com.github.sorusclient.client.module.impl.environmentchanger;

import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.ConfigurableData;

import java.util.List;

public class EnvironmentChanger extends ModuleDisableable {

    //TODO: Make world lighting reflect time of day
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
    public void addSettings(List<ConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new ConfigurableData.Toggle("Modify Time", this.modifyTime));
        settings.add(new ConfigurableData.Slider("Time", this.time, 0.0, 24000.0));
        settings.add(new ConfigurableData.Toggle("Modify Weather", this.modifyWeather));
        settings.add(new ConfigurableData.ClickThrough("Weather", this.weather));
    }

    public enum Weather {
        CLEAR,
        RAIN,
    }

}
