package com.github.sorusclient.client.module.impl.zoom;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.IAdapter;
import com.github.sorusclient.client.adapter.ScreenType;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.adapter.event.KeyEvent;
import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.adapter.Key;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.ConfigurableData;

import java.util.List;

public class Zoom extends ModuleDisableable {

    private final Setting<Key> key;
    private final Setting<Double> fov;
    private final Setting<Double> sensitivity;
    private final Setting<Boolean> cinematicCamera;

    private boolean toggled;

    public Zoom() {
        super("zoom");

        this.register("key", this.key = new Setting<>(Key.C));
        this.register("fov", this.fov = new Setting<>(30.0));
        this.register("sensitivity", this.sensitivity = new Setting<>(0.5));
        this.register("cinematicCamera", this.cinematicCamera = new Setting<>(false));

        Sorus.getInstance().get(EventManager.class).register(KeyEvent.class, this::onKey);
    }

    private void onKey(KeyEvent event) {
        if (Sorus.getInstance().get(IAdapter.class).getOpenScreen() == ScreenType.IN_GAME) {
            if (event.getKey().equals(this.key.getValue()) && !event.isRepeat()) {
                this.toggled = event.isPressed();
            }
        }
    }

    public boolean applyZoom() {
        return this.isEnabled() && this.toggled;
    }

    public double getFov() {
        return this.fov.getValue();
    }

    public double getSensitivity() {
        return this.sensitivity.getValue();
    }

    public boolean useCinematicCamera() {
        return this.cinematicCamera.getValue();
    }

    @Override
    public void addSettings(List<ConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new ConfigurableData.KeyBind("Key", this.key));
        settings.add(new ConfigurableData.Slider("Field Of View", this.fov, 15.0, 100.0));
        settings.add(new ConfigurableData.Slider("Sensitivity", this.sensitivity, 0.25, 1.5));
        settings.add(new ConfigurableData.Toggle("Cinematic Camera", this.cinematicCamera));
    }

}
