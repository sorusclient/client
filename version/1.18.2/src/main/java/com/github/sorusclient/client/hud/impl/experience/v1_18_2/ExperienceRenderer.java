/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.experience.v1_18_2;

import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.adapter.v1_18_2.RenderUtil;
import com.github.sorusclient.client.bootstrap.Initializer;
import com.github.sorusclient.client.hud.impl.experience.IExperienceRenderer;
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem;

public class ExperienceRenderer implements IExperienceRenderer, Initializer {

    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }

    @Override
    public void renderExperienceBar(double x, double y, double scale, double percent) {
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, new v1_18_2.net.minecraft.util.Identifier("textures/gui/icons.png"));

        RenderUtil.drawTexture(x, y, 0.0, 64.0, 183 * scale, 5 * scale, 183, 5);
        RenderUtil.drawTexture(x, y, 0.0, 69.0, (183 * percent) * scale, 5 * scale, (int) (183 * percent), 5);
    }

}
