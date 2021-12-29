package com.github.sorusclient.client.module;

import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.SettingConfigurableData;

import java.util.List;

public class ModuleDisableable extends Module {

    private final Setting<Boolean> enabled;

    public ModuleDisableable(String id) {
        super(id);

        this.register("enabled", this.enabled = new Setting<>(false));
    }

    public void setEnabled(boolean enabled) {
        this.enabled.setValue(enabled);
    }

    public boolean isEnabled() {
        return this.enabled.getValue();
    }

    @Override
    public void addSettings(List<SettingConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new SettingConfigurableData("Enabled", this.enabled, SettingConfigurableData.ConfigurableType.TOGGLE));
    }

}
