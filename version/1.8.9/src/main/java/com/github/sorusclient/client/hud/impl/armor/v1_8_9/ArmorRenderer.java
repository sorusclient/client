/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.armor.v1_8_9;

import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.adapter.v1_8_9.RenderUtil;
import com.github.sorusclient.client.adapter.IItem;
import com.github.sorusclient.client.adapter.v1_8_9.Util;
import com.github.sorusclient.client.bootstrap.Initializer;
import com.github.sorusclient.client.hud.impl.armor.IArmorRenderer;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.item.Item;
import v1_8_9.net.minecraft.item.ItemStack;
import v1_8_9.net.minecraft.util.Identifier;

public class ArmorRenderer implements IArmorRenderer, Initializer {

    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }

    @Override
    public void render(@NotNull IItem item, double x, double y, double scale) {
        var itemStack = (ItemStack) item.getInner();
        if (itemStack == null) {
            val id = Item.getRawId(Util.getItemByItemType(item.getType()));
            if (id != -1) {
                itemStack = new ItemStack(Item.byRawId(id));
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, 0.0);
        GlStateManager.scaled(scale * 18 / 16, scale * 18 / 16, 0.0);
        MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, 0, 0);
        GlStateManager.popMatrix();
    }

    @Override
    public void renderArmorPlateBackground(double x, double y, double scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, 0.0);
        GlStateManager.scaled(scale, scale, 1.0);
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/icons.png"));

        RenderUtil.drawTexture(0, 0, 16.0, 9.0, 9, 9);

        GlStateManager.popMatrix();
    }

    @Override
    public void renderArmorPlate(double x, double y, double scale, ArmorRenderType armorRenderType) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, 0.0);
        GlStateManager.scaled(scale, scale, 1.0);
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/icons.png"));

        if (armorRenderType == ArmorRenderType.FULL) {
            RenderUtil.drawTexture(0, 0, 43.0, 9.0, 9, 9);
        } else if (armorRenderType == ArmorRenderType.HALF) {
            RenderUtil.drawTexture(0, 0, 25.0, 9.0, 9, 9);
        }

        GlStateManager.popMatrix();
    }

}
