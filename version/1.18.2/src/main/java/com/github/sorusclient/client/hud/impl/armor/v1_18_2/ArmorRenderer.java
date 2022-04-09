/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.armor.v1_18_2;

import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.adapter.v1_18_2.RenderUtil;
import com.github.sorusclient.client.adapter.IItem;
import com.github.sorusclient.client.adapter.v1_18_2.Util;
import com.github.sorusclient.client.bootstrap.Initializer;
import com.github.sorusclient.client.hud.impl.armor.IArmorRenderer;
import lombok.val;
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem;
import v1_18_2.net.minecraft.client.MinecraftClient;
import v1_18_2.net.minecraft.item.ItemStack;

public class ArmorRenderer implements IArmorRenderer, Initializer {

    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }

    @Override
    public void render(IItem item, double x, double y, double scale) {
        var itemStack = (ItemStack) item.getInner();
        if (itemStack == null) {
            val item1 = Util.getItemByItemType(item.getType());
            if (item1 != null) {
                itemStack = new ItemStack(item1);
            }
        }

        MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides(itemStack, (int) x, (int) y);
    }

    @Override
    public void renderArmorPlateBackground(double x, double y, double scale) {
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, new v1_18_2.net.minecraft.util.Identifier("textures/gui/icons.png"));

        RenderUtil.drawTexture(x, y, 16.0, 9.0, 9 * scale, 9 * scale, 9, 9);
    }

    @Override
    public void renderArmorPlate(double x, double y, double scale, ArmorRenderType armorRenderType) {
        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, new v1_18_2.net.minecraft.util.Identifier("textures/gui/icons.png"));

        if (armorRenderType == ArmorRenderType.FULL) {
            RenderUtil.drawTexture(x, y, 43.0, 9.0, 9 * scale, 9 * scale, 9, 9);
        } else if (armorRenderType == ArmorRenderType.HALF) {
            RenderUtil.drawTexture(x, y, 25.0, 9.0, 9 * scale, 9 * scale, 9, 9);
        }
    }

}
