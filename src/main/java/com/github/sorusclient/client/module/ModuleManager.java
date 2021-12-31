package com.github.sorusclient.client.module;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.module.impl.blockoverlay.BlockOverlay;
import com.github.sorusclient.client.module.impl.fullbright.FullBright;
import com.github.sorusclient.client.module.impl.itemphysics.ItemPhysics;
import com.github.sorusclient.client.module.impl.oldanimations.OldAnimations;
import com.github.sorusclient.client.module.impl.environmentchanger.EnvironmentChanger;
import com.github.sorusclient.client.module.impl.perspective.Perspective;
import com.github.sorusclient.client.module.impl.zoom.Zoom;
import com.github.sorusclient.client.setting.SettingManager;
import com.github.sorusclient.client.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {

    private final Map<Class<Module>, ModuleData> modules = new HashMap<>();

    public void initialize() {
        this.registerInternalModules();
    }

    private void registerInternalModules() {
        this.register(new BlockOverlay(), "BlockOverlay", "test");
        this.register(new FullBright(), "FullBright", "test");
        this.register(new ItemPhysics(), "ItemPhysics", "test");
        this.register(new OldAnimations(), "OldAnimations", "test");
        this.register(new Perspective(), "Perspective", "test");
        this.register(new EnvironmentChanger(), "EnvironmentChanger", "test");
        this.register(new Zoom(), "Zoom", "test");
    }

    @SuppressWarnings("unchecked")
    public void register(Module module, String name, String description) {
        this.modules.put((Class<Module>) module.getClass(), new ModuleData(module, name, description));
        Sorus.getInstance().get(SettingManager.class).register(module);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> moduleClass) {
        return (T) this.modules.get(moduleClass).getModule();
    }

    public List<ModuleData> getModules() {
        return new ArrayList<>(this.modules.values());
    }

}
