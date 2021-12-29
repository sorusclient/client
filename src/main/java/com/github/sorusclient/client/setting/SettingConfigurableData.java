package com.github.sorusclient.client.setting;

public class SettingConfigurableData {

    private final String displayName;
    private final Setting<?> setting;
    private final ConfigurableType type;
    private final Object[] arguments;

    public SettingConfigurableData(String displayName, Setting<?> setting, ConfigurableType type, Object... arguments) {
        this.displayName = displayName;
        this.setting = setting;
        this.type = type;
        this.arguments = arguments;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Setting<?> getSetting() {
        return setting;
    }

    public ConfigurableType getType() {
        return type;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public enum ConfigurableType {
        SLIDER,
        COLOR_PICKER,
        TOGGLE,
        CLICK_THROUGH,
        KEYBIND,
    }

}
