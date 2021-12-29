package com.github.sorusclient.client.ui.framework.constraint;

import com.github.sorusclient.client.ui.framework.Component;
import com.github.sorusclient.client.util.Color;

import java.util.Map;
import java.util.function.Function;

public class Dependent implements Constraint {

    private final Function<Map<String, Object>, Object> function;

    public Dependent(Function<Map<String, Object>, Object> function) {
        this.function = function;
    }

    @Override
    public double getXValue(Component.Runtime componentRuntime) {
        Object value = this.function.apply(componentRuntime.getAvailableState());
        if (value instanceof Constraint) {
            return ((Constraint) value).getXValue(componentRuntime);
        } else {
            return (double) value;
        }
    }

    @Override
    public double getYValue(Component.Runtime componentRuntime) {
        Object value = this.function.apply(componentRuntime.getAvailableState());
        if (value instanceof Constraint) {
            return ((Constraint) value).getYValue(componentRuntime);
        } else {
            return (double) value;
        }
    }

    @Override
    public double getWidthValue(Component.Runtime componentRuntime) {
        return 0;
    }

    @Override
    public double getHeightValue(Component.Runtime componentRuntime) {
        return 0;
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
        return (Color) this.apply(componentRuntime);
    }

    @Override
    public String getStringValue(Component.Runtime componentRuntime) {
        return null;
    }

    private Object apply(Component.Runtime componentRuntime) {
        return this.function.apply(componentRuntime.getAvailableState());
    }

}
