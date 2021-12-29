package com.github.sorusclient.client.module;

import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.SettingContainer;
import com.github.sorusclient.client.setting.Util;
import com.github.sorusclient.client.util.Pair;

import java.util.*;

public class Module implements SettingContainer {

    private final String id;
    private final Map<String, Setting<?>> settings = new HashMap<>();

    protected final Setting<Boolean> isShared;

    public Module(String id) {
        this.id = id;

        this.isShared = new Setting<>(false);
    }

    protected void register(String id, Setting<?> setting) {
        this.settings.put(id, setting);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void load(Map<String, Object> settings) {
        for (Map.Entry<String, Object> setting : settings.entrySet()) {
            Setting<?> setting1 = this.settings.get(setting.getKey());
            if (setting1 != null) {
                setting1.setValueRaw(Util.toJava(setting1.getType(), setting.getValue()));
            }
        }
    }

    @Override
    public Map<String, Object> save() {
        Map<String, Object> settingsMap = new HashMap<>();
        for (Map.Entry<String, Setting<?>> setting : this.settings.entrySet()) {
            settingsMap.put(setting.getKey(), Util.toData(setting.getValue().getValue()));
        }
        return settingsMap;
    }

    public List<Pair<Pair<String, Setting<?>>, Pair<String, Object>>> getSettings() {
        return Collections.emptyList();
    }

    @Override
    public boolean isShared() {
        return this.isShared.getValue();
    }

    @Override
    public void setShared(boolean isShared) {
        this.isShared.setValue(isShared);
    }

}
