/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter;

import com.github.sorusclient.client.util.Color;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface IRenderer {

    void draw(RenderBuffer buffer);
    void setColor(Color color);

    default void drawRectangle(double x, double y, double width, double height, Color color) {
        this.drawRectangle(x, y, width, height, 0.0, color);
    }
    default void drawRectangle(double x, double y, double width, double height, double cornerRadius, Color color) {
        this.drawRectangle(x, y, width, height, cornerRadius, color, color, color, color);
    }
    void drawRectangle(double x, double y, double width, double height, double cornerRadius, Color topLeftColor, Color bottomLeftColor, Color bottomRightColor, Color topRightColor);

    default void drawRectangleBorder(double x, double y, double width, double height, double thickness, Color color) {
        drawRectangleBorder(x, y, width, height, 0.0, thickness, color);
    }

    void drawRectangleBorder(double x, double y, double width, double height, double cornerRadius, double thickness, Color color);

    default void drawImage(String id, double x, double y, double width, double height, Color color) {
        drawImage(id, x, y, width, height, true, color);
    }

    default void drawImage(String id, double x, double y, double width, double height, boolean antialias, Color color) {
        drawImage(id, x, y, width, height, 0.0, antialias, color);
    }

    default void drawImage(String id, double x, double y, double width, double height, double cornerRadius, boolean antialias, Color color) {
        drawImage(id, x, y, width, height, cornerRadius, 0.0, 0.0, 1.0, 1.0, antialias, color);
    }

    void drawImage(String id, double x, double y, double width, double height, double cornerRadius, double textureX, double textureY, double textureWidth, double textureHeight, boolean antialias, Color color);

    default void createTexture(String path) {
        var resource = IRenderer.class.getClassLoader().getResource(path);
        if (resource != null) {
            createTexture(path, resource);
        }
    }

    default void createTexture(String id, URL url) {
        try {
            createTexture(id, url.openStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    default void createTexture(String id, InputStream inputStream, boolean antialias) {
        try {
            createTexture(id, IOUtils.toByteArray(inputStream), antialias);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void createTexture(String id, byte[] bytes, boolean antialias);

    void scissor(double x, double y, double width, double height);
    void endScissor();

    IFontRenderer getFontRenderer(String id);

    void drawText(String id, String text, double x, double y, double scale, Color color);
    double getTextWidth(String fontId, String text);
    double getTextHeight(String fontId);

    default void createFont(String path) {
        createFont(path, IRenderer.class.getClassLoader().getResource(path));
    }
    default void createFont(String id, URL url) {
        try {
            createFont(id, url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void createFont(String id, InputStream inputStream);

    void loadBlur();
    void unloadBlur();

}
