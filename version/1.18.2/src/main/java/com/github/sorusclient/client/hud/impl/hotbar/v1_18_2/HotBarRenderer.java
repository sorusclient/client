/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.hotbar.v1_18_2;

import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.adapter.v1_18_2.RenderUtil;
import com.github.sorusclient.client.adapter.IItem;
import com.github.sorusclient.client.bootstrap.Initializer;
import com.github.sorusclient.client.hud.impl.hotbar.IHotBarRenderer;
import org.jetbrains.annotations.NotNull;
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem;
import v1_18_2.net.minecraft.client.MinecraftClient;
import v1_18_2.net.minecraft.item.ItemStack;

public class HotBarRenderer implements IHotBarRenderer, Initializer {

    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }

    @Override
    public void renderBackground(double x, double y, double scale) {
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, new v1_18_2.net.minecraft.util.Identifier("textures/gui/widgets.png"));
        RenderUtil.drawTexture(x, y, 0.0, 0.0, 182 * scale, 22 * scale, 182, 22);
    }

    @Override
    public void renderItem(double x, double y, double scale, @NotNull IItem item) {
        MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides((ItemStack) item.getInner(), (int) x, (int) y);
        MinecraftClient.getInstance().getItemRenderer().renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, (ItemStack) item.getInner(), (int) x, (int) y, null);
    }

    @Override
    public void renderSelectedSlot(double x, double y, double scale) {
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, new v1_18_2.net.minecraft.util.Identifier("textures/gui/widgets.png"));
        RenderUtil.drawTexture(x, y, 0.0, 22.0, 24 * scale, 24 * scale, 24, 24);
    }

}
