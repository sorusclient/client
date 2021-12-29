package com.github.sorusclient.client.module.impl.environmentchanger;

import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentChanger extends ModuleDisableable {

    private final Setting<Boolean> modifyTime;
    private final Setting<Long> time;
    private final Setting<Weather> weather;

    public EnvironmentChanger() {
        super("environmentChanger");

        this.register("modifyTime", this.modifyTime = new Setting<>(false));
        this.register("time", this.time = new Setting<>(5000L));
        this.register("weather", this.weather = new Setting<>(Weather.DEFAULT));
    }

    public boolean modifyTime() {
        return this.modifyTime.getValue();
    }

    public long getTime() {
        return this.time.getValue();
    }

    public Weather getWeather() {
        return this.weather.getValue();
    }

    @Override
    public List<Pair<Pair<String, Setting<?>>, Pair<String, Object>>> getSettings() {
        List<Pair<Pair<String, Setting<?>>, Pair<String, Object>>> settings = new ArrayList<>();

        settings.add(new Pair<>(new Pair<>("Modify Time", this.modifyTime), new Pair<>("TOGGLE", null)));
        settings.add(new Pair<>(new Pair<>("Time", this.time), new Pair<>("SLIDER", new Pair<>(0.0, 24000.0))));

        return settings;
    }

    public enum Weather {
        RAIN,
        CLEAR,
        DEFAULT
    }

}
