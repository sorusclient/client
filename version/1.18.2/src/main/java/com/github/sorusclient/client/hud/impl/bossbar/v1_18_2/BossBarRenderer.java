/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.bossbar.v1_18_2;

import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.adapter.v1_18_2.RenderUtil;
import com.github.sorusclient.client.adapter.BossBarColor;
import com.github.sorusclient.client.bootstrap.Initializer;
import com.github.sorusclient.client.hud.impl.bossbar.IBossBarRenderer;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem;

public class BossBarRenderer implements IBossBarRenderer, Initializer {

    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }

    @Override
    public void renderBossBar(double x, double y, double scale, double percent, @NotNull BossBarColor color) {
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, new v1_18_2.net.minecraft.util.Identifier("textures/gui/bars.png"));

        val textureY = color.ordinal() * 10.0;

        RenderUtil.drawTexture(x, y, 0.0, textureY, 183 * scale, 5 * scale, 183, 5);
        RenderUtil.drawTexture(x, y, 0.0, textureY + 5.0, (183 * percent) * scale, 5 * scale, (int) (183 * percent), 5);
    }

}
