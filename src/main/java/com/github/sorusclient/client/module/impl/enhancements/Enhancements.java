package com.github.sorusclient.client.module.impl.enhancements;

import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.ConfigurableData;

import java.util.List;

public class Enhancements extends ModuleDisableable {

    private final Setting<Double> fireHeight;
    private final Setting<Boolean> centeredInventory;

    public Enhancements() {
        super("enhancements");

        this.register("fireHeight", this.fireHeight = new Setting<>(0.0));
        this.register("centeredInventory", this.centeredInventory = new Setting<>(false));
    }

    public double getFireHeight() {
        return this.fireHeight.getValue();
    }

    public boolean isCenteredInventory() {
        return this.centeredInventory.getValue();
    }

    @Override
    public void addSettings(List<ConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new ConfigurableData.Slider("Fire Height", this.fireHeight, 0.0, 1.0));
        settings.add(new ConfigurableData.Toggle("Centered Inventory", this.centeredInventory));
    }

}
