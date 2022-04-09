/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2;

import com.github.sorusclient.client.adapter.IFontRenderer;
import com.github.sorusclient.client.adapter.IText;
import com.github.sorusclient.client.util.Color;
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem;
import v1_18_2.net.minecraft.client.font.TextRenderer;
import v1_18_2.net.minecraft.client.util.math.MatrixStack;

public class MinecraftFontRenderer implements IFontRenderer {

    private final TextRenderer textRenderer;

    public MinecraftFontRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    public void drawString(String text, double x, double y, double scale, Color color, boolean shadow) {
        var matrixStack = new MatrixStack();
        matrixStack.scale((float) scale, (float) scale, 1f);
        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.enableBlend();

        if (shadow) {
            textRenderer.drawWithShadow(matrixStack, text, (float) (x / scale), (float) (y / scale), color.getRgb());
        } else {
            textRenderer.draw(matrixStack, text, (float) (x / scale), (float) (y / scale), color.getRgb());
        }

        RenderSystem.enableDepthTest();
    }

    public void drawString(IText text, double x, double y, double scale, Color color, boolean shadow) {
        var matrixStack = new MatrixStack();
        matrixStack.scale((float) scale, (float) scale, 1);
        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.enableBlend();

        if (shadow) {
            textRenderer.drawWithShadow(matrixStack, Util.INSTANCE.apiTextToText(text), (float) (x / scale), (float) (y / scale), color.getRgb());
        } else {
            textRenderer.draw(matrixStack, Util.INSTANCE.apiTextToText(text), (float) (x / scale), (float) (y / scale), color.getRgb());
        }

        RenderSystem.enableDepthTest();
    }

    public double getWidth(String text) {
        return textRenderer.getWidth(text) - 1;
    }

    public double getWidth(IText text) {
        return textRenderer.getWidth(Util.INSTANCE.apiTextToText(text)) - 1;
    }

    public double getHeight() {
        return textRenderer.fontHeight - 1.75;
    }

}
