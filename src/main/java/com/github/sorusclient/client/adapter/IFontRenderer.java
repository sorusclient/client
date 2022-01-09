package com.github.sorusclient.client.adapter;

import com.github.sorusclient.client.util.Color;

public interface IFontRenderer {

    void drawString(String text, double x, double y, double scale, Color color);
    double getWidth(String text);
    double getHeight();

    default void drawStringShadowed(String text, double x, double y, double scale, Color color, Color shadowColor) {
        this.drawString(text, x + 1 * scale, y + 1 * scale, scale, shadowColor);
        this.drawString(text, x, y, scale, color);
    }

}
