package com.github.sorusclient.client;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.exception.NoSuchApiException;
import com.github.sorusclient.client.adapter.Key;
import com.github.sorusclient.client.adapter.MinecraftAdapter;
import com.github.sorusclient.client.adapter.ScreenType;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.event.impl.KeyEvent;
import com.github.sorusclient.client.event.impl.RenderEvent;
import com.github.sorusclient.client.hud.HUDManager;
import com.github.sorusclient.client.module.ModuleManager;
import com.github.sorusclient.client.setting.SettingManager;
import com.github.sorusclient.client.transform.TransformerManager;
import com.github.sorusclient.client.ui.Renderer;
import com.github.sorusclient.client.ui.UserInterface;
import com.github.sorusclient.client.ui.framework.Container;
import com.github.sorusclient.client.ui.framework.ContainerRenderer;
import com.github.sorusclient.client.ui.framework.List;
import com.github.sorusclient.client.ui.framework.TabHolder;
import com.github.sorusclient.client.ui.framework.constraint.*;
import com.github.sorusclient.client.util.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Sorus {

    public static Sorus getInstance() {
        try {
            return GlassLoader.getInstance().getAPI(Sorus.class);
        } catch (NoSuchApiException ignored) {
            Sorus sorus = new Sorus();
            GlassLoader.getInstance().registerAPI(sorus);
            sorus.initialize();
            return sorus;
        }
    }

    private final Map<Class<?>, Object> components = new HashMap<>();

    public void initialize() {
        this.register(new ContainerRenderer());
        this.register(new EventManager());
        this.register(new HUDManager());
        this.register(new MinecraftAdapter());
        this.register(new ModuleManager());
        this.register(new Renderer());
        this.register(new SettingManager());
        this.register(new TransformerManager());
        this.register(new UserInterface());

        this.get(HUDManager.class).initialize();
        this.get(ModuleManager.class).initialize();
        this.get(ContainerRenderer.class).initialize();
        this.get(UserInterface.class).initialize();

        SettingManager settingManager = this.get(SettingManager.class);
        settingManager.loadProfiles();
        settingManager.load("/");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.get(SettingManager.class).saveCurrent()));
    }

    public void register(Object component) {
        this.components.put(component.getClass(), component);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> componentClass) {
        return (T) this.components.get(componentClass);
    }

}
