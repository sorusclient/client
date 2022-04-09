/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter;

import com.github.sorusclient.client.util.Color;

public interface IFontRenderer {
    void drawString(String text, double x, double y, double scale, Color color, boolean shadow);
    double getWidth(String text);
    double getWidth(IText text);
    double getHeight();
    default void drawString(String text, double x, double y, double scale, Color color) {
        drawString(text, x, y, scale, color, false);
    }

    void drawString(IText text, double x, double y, double scale, Color color, boolean shadow);
    default void drawString(IText text, double x, double y, double scale, Color color) {
        drawString(text, x, y, scale, color, false);
    }
}
