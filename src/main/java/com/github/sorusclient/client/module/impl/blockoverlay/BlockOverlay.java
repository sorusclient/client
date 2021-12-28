package com.github.sorusclient.client.module.impl.blockoverlay;

import com.github.sorusclient.client.module.ModuleDisableable;
import com.github.sorusclient.client.setting.Setting;
import com.github.sorusclient.client.util.Color;

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

}
