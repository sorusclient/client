package com.github.sorusclient.client;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.sorusclient.client.adapter.IAdapter;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.hud.HUDManager;
import com.github.sorusclient.client.module.ModuleManager;
import com.github.sorusclient.client.plugin.PluginManager;
import com.github.sorusclient.client.server.ServerIntegrationManager;
import com.github.sorusclient.client.setting.SettingManager;
import com.github.sorusclient.client.ui.Renderer;
import com.github.sorusclient.client.ui.UserInterface;
import com.github.sorusclient.client.ui.framework.ContainerRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Sorus implements Listener {

    @Override
    public void run() {
        GlassLoader.getInstance().runHooks("post-initialize");

        Sorus sorus = new Sorus();
        GlassLoader.getInstance().registerAPI(sorus);
        sorus.initialize();
    }

    public static Sorus getInstance() {
        return Objects.requireNonNull(GlassLoader.getInstance().getAPI(Sorus.class));
    }

    private final Map<Class<?>, Object> components = new HashMap<>();

    public void initialize() {
        this.register(new ModuleManager());
        this.register(new ContainerRenderer());
        this.register(new EventManager());
        this.register(new HUDManager());
        this.register(IAdapter.class, GlassLoader.getInstance().getInterface(IAdapter.class));
        this.register(new PluginManager());
        this.register(new Renderer());
        this.register(new ServerIntegrationManager());
        this.register(new SettingManager());
        this.register(new UserInterface());

        this.get(HUDManager.class).initialize();
        this.get(ModuleManager.class).initialize();
        this.get(ContainerRenderer.class).initialize();

        SettingManager settingManager = this.get(SettingManager.class);
        settingManager.loadProfiles();
        settingManager.load("/");

        this.get(UserInterface.class).initialize();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.get(SettingManager.class).saveCurrent()));
    }

    public void register(Object component) {
        this.register(component.getClass(), component);
    }

    public void register(Class<?> clazz, Object component) {
        this.components.put(clazz, component);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> componentClass) {
        return (T) this.components.get(componentClass);
    }

    public String getClientBrand() {
        return "sorus";
    }

}
