/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IFontRenderer;
import com.github.sorusclient.client.adapter.IText;
import com.github.sorusclient.client.util.Color;
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager;
import v1_8_9.net.minecraft.client.font.TextRenderer;

public class MinecraftFontRenderer implements IFontRenderer {

    private final TextRenderer textRenderer;

    public MinecraftFontRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    @Override
    public void drawString(String text, double x, double y, double scale, Color color, boolean shadow) {
        GlStateManager.pushMatrix();
        GlStateManager.scaled(scale, scale, 1);
        GlStateManager.disableDepthTest();
        GlStateManager.enableTexture();
        GlStateManager.enableBlend();
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        textRenderer.draw(text, (float) (x / scale), (float) (y / scale), color.getRgb(), shadow);
        GlStateManager.enableDepthTest();
        GlStateManager.popMatrix();
    }

    @Override
    public double getWidth(String text) {
        return textRenderer.getStringWidth(text) - 1;
    }

    @Override
    public double getWidth(IText text) {
        return getWidth(Util.INSTANCE.apiTextToText(text).getString());
    }

    @Override
    public double getHeight() {
        return textRenderer.fontHeight - 1.75;
    }

    @Override
    public void drawString(IText text, double x, double y, double scale, Color color, boolean shadow) {
        drawString(Util.INSTANCE.apiTextToText(text).getString(), x, y, scale, color, shadow);
    }

}
