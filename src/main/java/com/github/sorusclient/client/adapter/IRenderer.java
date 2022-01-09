package com.github.sorusclient.client.adapter;

import com.github.sorusclient.client.util.Color;

public interface IRenderer {

    void draw(RenderBuffer buffer);

    void setColor(Color color);
    void setLineThickness(double thickness);

    default void drawRectangle(double x, double y, double width, double height, Color color) {
        this.drawRectangle(x, y, width, height, 0, color, color, color, color);
    }

    void drawRectangle(double x, double y, double width, double height, double cornerRadius, Color topLeftColor, Color bottomLeftColor, Color bottomRightColor, Color topRightColor);
    void drawImage(String imagePath, double x, double y, double width, double height, Color color);
    IFontRenderer getFontRenderer(String id);

}
