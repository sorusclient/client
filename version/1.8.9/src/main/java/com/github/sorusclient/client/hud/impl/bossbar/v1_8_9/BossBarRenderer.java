/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.bossbar.v1_8_9;

import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.adapter.BossBarColor;
import com.github.sorusclient.client.bootstrap.Initializer;
import com.github.sorusclient.client.hud.impl.bossbar.IBossBarRenderer;
import lombok.val;
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.gui.DrawableHelper;
import v1_8_9.net.minecraft.util.Identifier;

public class BossBarRenderer implements IBossBarRenderer, Initializer {

    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }

    @Override
    public void renderBossBar(double x, double y, double scale, double percent, BossBarColor color) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, 0.0);
        GlStateManager.scaled(scale, scale, 1.0);
        GlStateManager.color4f(1f, 1f, 1f, 1f);

        val drawableHelper = new DrawableHelper();
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/icons.png"));
        drawableHelper.drawTexture(0, 0, 0, 74, 183, 5);
        drawableHelper.drawTexture(0, 0, 0, 79, (int) (183 * percent), 5);

        GlStateManager.popMatrix();
    }

}
