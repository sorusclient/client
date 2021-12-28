package com.github.sorusclient.client.ui.framework.constraint;

import com.github.sorusclient.client.ui.framework.Component;
import com.github.sorusclient.client.util.Color;

public interface Constraint {

    double getXValue(Component.Runtime componentRuntime);
    double getYValue(Component.Runtime componentRuntime);
    double getWidthValue(Component.Runtime componentRuntime);
    double getHeightValue(Component.Runtime componentRuntime);
    double getCornerRadiusValue(Component.Runtime componentRuntime);

    double getPaddingValue(Component.Runtime componentRuntime);

    Color getColorValue(Component.Runtime componentRuntime);
    String getStringValue(Component.Runtime componentRuntime);

}
