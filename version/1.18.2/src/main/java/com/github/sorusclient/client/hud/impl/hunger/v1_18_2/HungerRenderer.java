/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.hunger.v1_18_2;

import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.adapter.v1_18_2.RenderUtil;
import com.github.sorusclient.client.bootstrap.Initializer;
import com.github.sorusclient.client.hud.impl.hunger.IHungerRenderer;
import lombok.val;
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem;

public class HungerRenderer implements IHungerRenderer, Initializer {

    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }

    @Override
    public void renderHunger(double x, double y, double scale, HeartRenderType heartRenderType) {
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, new v1_18_2.net.minecraft.util.Identifier("textures/gui/icons.png"));

        val xLocation = 52;
        if (heartRenderType == IHungerRenderer.HeartRenderType.FULL) {
            RenderUtil.drawTexture(x, y, xLocation, 27.0, 9 * scale, 9 * scale, 9, 9);
        } else if (heartRenderType == IHungerRenderer.HeartRenderType.HALF) {
            RenderUtil.drawTexture(x, y, xLocation + 9, 27.0, 9 * scale, 9 * scale, 9, 9);
        }
    }

    @Override
    public void renderHungerBackground(double x, double y, double scale) {
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, new v1_18_2.net.minecraft.util.Identifier("textures/gui/icons.png"));

        RenderUtil.drawTexture(x, y, 16.0, 27.0, 9 * scale, 9 * scale, 9, 9);
    }

}
