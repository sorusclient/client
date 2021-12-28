package com.github.sorusclient.client.module.impl.zoom;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.MinecraftAdapter;
import com.github.sorusclient.client.adapter.ScreenType;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.event.impl.KeyEvent;
import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.adapter.Key;
import com.github.sorusclient.client.setting.Setting;

public class Zoom extends ModuleDisableable {

    private final Setting<Key> key;
    private final Setting<Integer> fov;
    private final Setting<Double> sensitivity;
    private final Setting<Boolean> cinematicCamera;

    private boolean toggled;

    public Zoom() {
        super("zoom");

        this.register("key", this.key = new Setting<>(Key.C));
        this.register("fov", this.fov = new Setting<>(30));
        this.register("sensitivity", this.sensitivity = new Setting<>(0.5));
        this.register("cinematicCamera", this.cinematicCamera = new Setting<>(false));

        Sorus.getInstance().get(EventManager.class).register(KeyEvent.class, this::onKey);
    }

    private void onKey(KeyEvent event) {
        if (Sorus.getInstance().get(MinecraftAdapter.class).getOpenScreen() == ScreenType.IN_GAME) {
            if (event.getKey().equals(this.key.getValue()) && !event.isRepeat()) {
                this.toggled = event.isPressed();
            }
        }
    }

    public boolean applyZoom() {
        return this.isEnabled() && this.toggled;
    }

    public long getFov() {
        return this.fov.getValue();
    }

    public double getSensitivity() {
        return this.sensitivity.getValue();
    }

    public boolean useCinematicCamera() {
        return this.cinematicCamera.getValue();
    }

}
