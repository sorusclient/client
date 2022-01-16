package com.github.sorusclient.client.module;

import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.ConfigurableData;

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
    public void addSettings(List<ConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new ConfigurableData.Toggle("Enabled", this.enabled));
    }

}
