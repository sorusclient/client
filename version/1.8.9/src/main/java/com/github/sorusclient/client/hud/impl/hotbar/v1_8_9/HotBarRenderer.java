/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.hotbar.v1_8_9;

import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.adapter.IItem;
import com.github.sorusclient.client.bootstrap.Initializer;
import com.github.sorusclient.client.hud.impl.hotbar.IHotBarRenderer;
import lombok.val;
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.gui.DrawableHelper;
import v1_8_9.net.minecraft.client.render.GuiLighting;
import v1_8_9.net.minecraft.item.ItemStack;
import v1_8_9.net.minecraft.util.Identifier;

public class HotBarRenderer implements IHotBarRenderer, Initializer {

    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }

    @Override
    public void renderBackground(double x, double y, double scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, 0.0);
        GlStateManager.scaled(scale, scale, 1.0);
        GlStateManager.color4f(1f, 1f, 1f, 1f);

        GlStateManager.enableBlend();
        GlStateManager.enableTexture();

        val drawableHelper = new DrawableHelper();
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/widgets.png"));
        drawableHelper.drawTexture(0, 0, 0, 0, 182, 22);

        GlStateManager.popMatrix();
    }

    @Override
    public void renderItem(double x, double y, double scale, IItem item) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, 0.0);
        GlStateManager.scaled(scale, scale, 1.0);

        GlStateManager.color4f(1f, 1f, 1f, 1f);
        GuiLighting.enable();

        MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides((ItemStack) item.getInner(), 0, 0);
        MinecraftClient.getInstance().getItemRenderer().renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, (ItemStack) item.getInner(), 0, 0, null);

        GuiLighting.disable();
        GlStateManager.popMatrix();
    }

    @Override
    public void renderSelectedSlot(double x, double y, double scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, 0.0);
        GlStateManager.scaled(scale, scale, 1.0);

        GlStateManager.color4f(1f, 1f, 1f, 1f);

        val drawableHelper = new DrawableHelper();
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/widgets.png"));
        drawableHelper.drawTexture(0, 0, 0, 22, 24, 24);

        GlStateManager.popMatrix();
    }

}
