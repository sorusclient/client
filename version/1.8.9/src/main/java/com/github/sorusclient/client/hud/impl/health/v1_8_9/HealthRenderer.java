/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.health.v1_8_9;

import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.adapter.v1_8_9.RenderUtil;
import com.github.sorusclient.client.bootstrap.Initializer;
import com.github.sorusclient.client.hud.impl.health.IHealthRenderer;
import lombok.val;
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.util.Identifier;

public class HealthRenderer implements IHealthRenderer, Initializer {

    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }

    @Override
    public void renderHeart(double x, double y, double scale, HeartType heartType, HeartRenderType heartRenderType) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, 0.0);
        GlStateManager.scaled(scale, scale, 1.0);
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        GlStateManager.enableBlend();

        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/icons.png"));

        val xLocation = heartType == HeartType.HEALTH ? 52 : 52 + 12 * 9;
        switch (heartRenderType) {
            case FULL -> RenderUtil.drawTexture(0, 0, xLocation, 0.0, 9, 9);
            case HALF_EMPTY -> RenderUtil.drawTexture(0, 0, xLocation + 9, 0.0, 9, 9);
            case HALF_DAMAGE -> {
                RenderUtil.drawTexture(0, 0, xLocation + 18, 0.0, 9, 9);
                RenderUtil.drawTexture(0, 0, xLocation + 9, 0.0, 9, 9);
            }
            case DAMAGE -> RenderUtil.drawTexture(0, 0, xLocation + 18, 0.0, 9, 9);
            case DAMAGE_EMPTY -> RenderUtil.drawTexture(0, 0, xLocation + 27, 0.0, 9, 9);
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void renderHeartBackground(double x, double y, double scale, BackgroundType backgroundType) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, 0.0);
        GlStateManager.scaled(scale, scale, 1.0);

        GlStateManager.color4f(1f, 1f, 1f, 1f);
        GlStateManager.enableBlend();

        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/icons.png"));

        switch (backgroundType) {
            case STANDARD -> RenderUtil.drawTexture(0, 0, 16.0, 0.0, 9, 9);
            case FLASHING_OUTLINE -> RenderUtil.drawTexture(0, 0, 25.0, 0.0, 9, 9);
        }

        GlStateManager.popMatrix();
    }

}
