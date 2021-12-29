package com.github.sorusclient.client.module.impl.blockoverlay;

import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.setting.SettingConfigurableData;
import com.github.sorusclient.client.util.Color;

import java.util.List;

public class BlockOverlay extends ModuleDisableable {

    private final Setting<Color> borderColor;
    private final Setting<Double> borderThickness;
    private final Setting<Color> fillColor;

    public BlockOverlay() {
        super("blockOverlay");

        this.register("borderColor", this.borderColor = new Setting<>(Color.BLACK));
        this.register("borderThickness", this.borderThickness = new Setting<>(1.0));
        this.register("fillColor", this.fillColor = new Setting<>(Color.fromRGB(0, 0, 0, 0)));
    }

    public Color getBorderColor() {
        return this.borderColor.getValue();
    }

    public double getBorderThickness() {
        return this.borderThickness.getValue();
    }

    public Color getFillColor() {
        return this.fillColor.getValue();
    }

    @Override
    public void addSettings(List<SettingConfigurableData> settings) {
        super.addSettings(settings);
        settings.add(new SettingConfigurableData("Border Thickness", this.borderThickness, SettingConfigurableData.ConfigurableType.SLIDER, 0.0, 5.0));
        settings.add(new SettingConfigurableData("Fill Color", this.fillColor, SettingConfigurableData.ConfigurableType.COLOR_PICKER));
    }

}
