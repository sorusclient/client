package com.github.sorusclient.client.module;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.module.impl.blockoverlay.BlockOverlay;
import com.github.sorusclient.client.module.impl.fullbright.FullBright;
import com.github.sorusclient.client.module.impl.itemphysics.ItemPhysics;
import com.github.sorusclient.client.module.impl.oldanimations.OldAnimations;
import com.github.sorusclient.client.module.impl.environmentchanger.EnvironmentChanger;
import com.github.sorusclient.client.module.impl.zoom.Zoom;
import com.github.sorusclient.client.setting.SettingManager;

import java.util.HashMap;
import java.util.Map;

public class ModuleManager {

    private final Map<Class<Module>, Module> modules = new HashMap<>();

    public void initialize() {
        this.registerInternalModules();
    }

    private void registerInternalModules() {
        this.register(new BlockOverlay());
        this.register(new FullBright());
        this.register(new ItemPhysics());
        this.register(new OldAnimations());
        this.register(new EnvironmentChanger());
        this.register(new Zoom());
    }

    @SuppressWarnings("unchecked")
    public void register(Module module) {
        this.modules.put((Class<Module>) module.getClass(), module);
        Sorus.getInstance().get(SettingManager.class).register(module);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> moduleClass) {
        return (T) this.modules.get(moduleClass);
    }

}
