package com.github.sorusclient.client.ui;

import com.github.glassmc.loader.GlassLoader;
import com.github.sorusclient.client.util.Color;

public class Renderer {

    public void drawRectangle(double x, double y, double width, double height, Color color) {
        this.drawRectangle(x, y, width, height, 0, color);
    }

    public void drawRectangle(double x, double y, double width, double height, double cornerRadius, Color color) {
        this.drawRectangle(x, y, width, height, cornerRadius, color, color, color, color);
    }

    public void drawRectangle(double x, double y, double width, double height, double cornerRadius, Color topLeftColor, Color bottomLeftColor, Color bottomRightColor, Color topRightColor) {
        GlassLoader.getInstance().getInterface(IRenderer.class).drawRectangle(x, y, width, height, cornerRadius, topLeftColor, bottomLeftColor, bottomRightColor, topRightColor);
    }

    public void drawImage(String imagePath, double x, double y, double width, double height, Color color) {
        GlassLoader.getInstance().getInterface(IRenderer.class).drawImage(imagePath, x, y, width, height, color);
    }

    public IFontRenderer getFontRenderer(String id) {
        return GlassLoader.getInstance().getInterface(IRenderer.class).getFontRenderer(id);
    }

}
