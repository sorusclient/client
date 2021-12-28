package com.github.sorusclient.client.ui.framework.constraint;

import com.github.sorusclient.client.ui.framework.Component;
import com.github.sorusclient.client.util.Color;

public class Absolute implements Constraint {

    private final Object value;

    public Absolute(double value) {
        this((Object) value);
    }

    public Absolute(Color value) {
        this((Object) value);
    }

    public Absolute(String value) {
        this((Object) value);
    }

    private Absolute(Object value) {
        this.value = value;
    }

    @Override
    public double getXValue(Component.Runtime componentRuntime) {
        return (double) this.value;
    }

    @Override
    public double getYValue(Component.Runtime componentRuntime) {
        return (double) this.value;
    }

    @Override
    public double getWidthValue(Component.Runtime componentRuntime) {
        return (double) this.value;
    }

    @Override
    public double getHeightValue(Component.Runtime componentRuntime) {
        return (double) this.value;
    }

    @Override
    public double getCornerRadiusValue(Component.Runtime componentRuntime) {
        return (double) this.value;
    }

    @Override
    public double getPaddingValue(Component.Runtime componentRuntime) {
        return (double) this.value;
    }

    @Override
    public Color getColorValue(Component.Runtime componentRuntime) {
        return (Color) this.value;
    }

    @Override
    public String getStringValue(Component.Runtime componentRuntime) {
        return (String) this.value;
    }

}
