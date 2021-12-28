package com.github.sorusclient.client.ui.framework.constraint;

import com.github.sorusclient.client.ui.framework.Component;
import com.github.sorusclient.client.util.Color;

public class Copy implements Constraint {

    private final double multiplier;

    public Copy() {
        this(1);
    }

    public Copy(double multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public double getXValue(Component.Runtime componentRuntime) {
        return 0;
    }

    @Override
    public double getYValue(Component.Runtime componentRuntime) {
        return 0;
    }

    @Override
    public double getWidthValue(Component.Runtime componentRuntime) {
        return componentRuntime.getHeight() * this.multiplier;
    }

    @Override
    public double getHeightValue(Component.Runtime componentRuntime) {
        return componentRuntime.getWidth() * this.multiplier;
    }

    @Override
    public double getCornerRadiusValue(Component.Runtime componentRuntime) {
        return 0;
    }

    @Override
    public double getPaddingValue(Component.Runtime componentRuntime) {
        return 0;
    }

    @Override
    public Color getColorValue(Component.Runtime componentRuntime) {
        return null;
    }

    @Override
    public String getStringValue(Component.Runtime componentRuntime) {
        return null;
    }

}
