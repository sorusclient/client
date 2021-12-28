package com.github.sorusclient.client.module.impl.environmentchanger;

import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.setting.Setting;

public class EnvironmentChanger extends ModuleDisableable {

    private final Setting<Long> time;
    private final Setting<Weather> weather;

    public EnvironmentChanger() {
        super("environmentChanger");

        this.register("time", this.time = new Setting<>(5000L));
        this.register("weather", this.weather = new Setting<>(Weather.DEFAULT));
    }

    public boolean modifyTime() {
        return false;
    }

    public long getTime() {
        return this.time.getValue();
    }

    public Weather getWeather() {
        return this.weather.getValue();
    }

    public enum Weather {
        RAIN,
        CLEAR,
        DEFAULT
    }

}
