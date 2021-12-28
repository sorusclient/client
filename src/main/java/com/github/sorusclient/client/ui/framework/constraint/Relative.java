package com.github.sorusclient.client.ui.framework.constraint;

import com.github.sorusclient.client.ui.framework.Component;
import com.github.sorusclient.client.util.Color;

public class Relative implements Constraint {

    private final double percent;

    public Relative(double percent) {
        this.percent = percent;
    }

    @Override
    public double getXValue(Component.Runtime componentRuntime) {
        return componentRuntime.getParent().getWidth() * this.percent;
    }

    @Override
    public double getYValue(Component.Runtime componentRuntime) {
        return componentRuntime.getParent().getHeight() * this.percent;
    }

    @Override
    public double getWidthValue(Component.Runtime componentRuntime) {
        return componentRuntime.getParent().getWidth() * this.percent;
    }

    @Override
    public double getHeightValue(Component.Runtime componentRuntime) {
        return componentRuntime.getParent().getHeight() * this.percent;
    }

    @Override
    public double getPaddingValue(Component.Runtime componentRuntime) {
        return componentRuntime.getParent().getWidth() * this.percent;
    }

    @Override
    public Color getColorValue(Component.Runtime componentRuntime) {
        return null;
    }

    @Override
    public String getStringValue(Component.Runtime componentRuntime) {
        return null;
    }

    @Override
    public double getCornerRadiusValue(Component.Runtime componentRuntime) {
        return componentRuntime.getParent().getWidth() * this.percent;
    }

}
