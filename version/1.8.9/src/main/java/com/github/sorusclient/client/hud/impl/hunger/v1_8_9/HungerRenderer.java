/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.hunger.v1_8_9;

import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.adapter.v1_8_9.RenderUtil;
import com.github.sorusclient.client.bootstrap.Initializer;
import com.github.sorusclient.client.hud.impl.hunger.IHungerRenderer;
import lombok.val;
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.util.Identifier;

public class HungerRenderer implements IHungerRenderer, Initializer {

    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }

    @Override
    public void renderHunger(double x, double y, double scale, HeartRenderType heartRenderType) {
        GlStateManager.enableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, 0.0);
        GlStateManager.scaled(scale, scale, 1.0);

        GlStateManager.color4f(1f, 1f, 1f, 1f);

        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/icons.png"));

        val xLocation = 52;
        if (heartRenderType == IHungerRenderer.HeartRenderType.FULL) {
            RenderUtil.drawTexture(0, 0, xLocation, 27.0, 9, 9);
        } else if (heartRenderType == IHungerRenderer.HeartRenderType.HALF) {
            RenderUtil.drawTexture(0, 0, xLocation + 9, 27.0, 9, 9);
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void renderHungerBackground(double x, double y, double scale) {
        GlStateManager.enableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, 0.0);
        GlStateManager.scaled(scale, scale, 1.0);

        GlStateManager.color4f(1f, 1f, 1f, 1f);

        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/icons.png"));
        RenderUtil.drawTexture(0, 0, 16.0, 27.0, 9, 9);

        GlStateManager.popMatrix();
    }

}
