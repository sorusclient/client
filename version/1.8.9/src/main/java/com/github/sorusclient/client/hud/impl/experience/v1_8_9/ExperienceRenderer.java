/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.experience.v1_8_9;

import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.bootstrap.Initializer;
import com.github.sorusclient.client.hud.impl.experience.IExperienceRenderer;
import lombok.val;
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.gui.DrawableHelper;
import v1_8_9.net.minecraft.util.Identifier;

public class ExperienceRenderer implements IExperienceRenderer, Initializer {

    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }

    @Override
    public void renderExperienceBar(double x, double y, double scale, double percent) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, 0.0);
        GlStateManager.scaled(scale, scale, 1.0);

        GlStateManager.color4f(1f, 1f, 1f, 1f);
        GlStateManager.enableTexture();

        val drawableHelper = new DrawableHelper();

        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/icons.png"));
        drawableHelper.drawTexture(0, 0, 0, 64, 183, 5);
        drawableHelper.drawTexture(0, 0, 0, 69 /* nice */, (int) (183 * percent), 5);
        GlStateManager.popMatrix();
    }

}
