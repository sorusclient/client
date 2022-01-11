package com.github.sorusclient.client.ui.framework.constraint;

import com.github.sorusclient.client.ui.framework.Component;
import com.github.sorusclient.client.util.Color;

import java.util.List;

public class Flexible implements Constraint {

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
        List<double[]> placedComponents = componentRuntime.getParent().getPlacedComponents();

        double maxLeft = -Double.MAX_VALUE;
        double maxRight = Double.MAX_VALUE;

        for (double[] component : placedComponents) {
            double padding = Math.max(componentRuntime.getPadding(), component[4]);

            boolean intersectingY = componentRuntime.getY() + componentRuntime.getHeight() / 2 + padding > component[1] - component[3] / 2 + 1 && componentRuntime.getY() - componentRuntime.getHeight() / 2 + 1 < component[1] + component[3] / 2 + padding;
            boolean canInteractLeft = component[0] < componentRuntime.getX();

            if (intersectingY && canInteractLeft) {
                double componentRight = component[0] + component[2] / 2 + padding;
                maxLeft = Math.max(maxLeft, componentRight);
            }

            boolean canInteractRight = component[0] > componentRuntime.getX();

            if (intersectingY && canInteractRight) {
                double componentLeft = component[0] - component[2] / 2 - padding;
                maxRight = Math.min(maxRight, componentLeft);
            }
        }

        return Math.max(0, maxRight - maxLeft);
    }

    @Override
    public double getHeightValue(Component.Runtime componentRuntime) {
        List<double[]> placedComponents = componentRuntime.getParent().getPlacedComponents();

        double maxTop = -Double.MAX_VALUE;
        double maxBottom = Double.MAX_VALUE;

        for (double[] component : placedComponents) {
            double padding = Math.max(componentRuntime.getPadding(), component[4]);

            boolean intersectingX = componentRuntime.getX() + componentRuntime.getWidth() / 2 + padding > component[0] - component[2] / 2 + 1 && componentRuntime.getX() - componentRuntime.getWidth() / 2 + 1 < component[0] + component[2] / 2 + padding;
            boolean canInteractTop = component[1] < componentRuntime.getY();

            if (intersectingX && canInteractTop) {
                double componentBottom = component[1] + component[3] / 2 + padding;
                maxTop = Math.max(maxTop, componentBottom);
            }

            boolean canInteractBottom = component[1] > componentRuntime.getY();

            if (intersectingX && canInteractBottom) {
                double componentTop = component[1] - component[3] / 2 - padding;
                maxBottom = Math.min(maxBottom, componentTop);
            }
        }

        return Math.max(0, maxBottom - maxTop);
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

    @Override
    public double getCornerRadiusValue(Component.Runtime componentRuntime) {
        return 0;
    }

}
