package com.github.sorusclient.client.ui;

import com.github.sorusclient.client.util.Color;

public interface IRenderer {

    void drawRectangle(double x, double y, double width, double height, double cornerRadius, Color color);
    void drawImage(String imagePath, double x, double y, double width, double height, Color color);
    IFontRenderer getFontRenderer(String id);

}
