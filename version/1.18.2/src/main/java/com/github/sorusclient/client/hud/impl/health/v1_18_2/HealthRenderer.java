/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.health.v1_18_2;

import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.adapter.v1_18_2.RenderUtil;
import com.github.sorusclient.client.bootstrap.Initializer;
import com.github.sorusclient.client.hud.impl.health.IHealthRenderer;
import lombok.val;
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem;
import v1_18_2.net.minecraft.util.Identifier;

public class HealthRenderer implements IHealthRenderer, Initializer {
    
    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }

    @Override
    public void renderHeart(double x, double y, double scale, HeartType heartType, HeartRenderType heartRenderType) {
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, new Identifier("textures/gui/icons.png"));

        val xLocation = heartType == HeartType.HEALTH ? 52 : 52 + 12 * 9;
        switch (heartRenderType) {
            case FULL -> RenderUtil.drawTexture(x, y, xLocation, 0.0, 9 * scale, 9 * scale, 9, 9);
            case HALF_EMPTY -> RenderUtil.drawTexture(x, y, xLocation + 9, 0.0, 9 * scale, 9 * scale, 9, 9);
            case HALF_DAMAGE -> {
                RenderUtil.drawTexture(x, y, xLocation + 18, 0.0, 9 * scale, 9 * scale, 9, 9);
                RenderUtil.drawTexture(x, y, xLocation + 9, 0.0, 9 * scale, 9 * scale, 9, 9);
            }
            case DAMAGE -> RenderUtil.drawTexture(x, y, xLocation + 18, 0.0, 9 * scale, 9 * scale, 9, 9);
            case DAMAGE_EMPTY -> RenderUtil.drawTexture(x, y, xLocation + 27, 0.0, 9 * scale, 9 * scale, 9, 9);
        }
    }

    @Override
    public void renderHeartBackground(double x, double y, double scale, BackgroundType backgroundType) {
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, new Identifier("textures/gui/icons.png"));

        switch (backgroundType) {
            case STANDARD -> RenderUtil.drawTexture(x, y, 16.0, 0.0, 9 * scale, 9 * scale, 9, 9);
            case FLASHING_OUTLINE -> RenderUtil.drawTexture(x, y, 25.0, 0.0, 9 * scale, 9 * scale, 9, 9);
        }
    }
    
}
