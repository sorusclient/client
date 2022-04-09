/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.potions.v1_8_9;

import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.adapter.PotionType;
import com.github.sorusclient.client.bootstrap.Initializer;
import com.github.sorusclient.client.hud.impl.potions.IPotionEffectRenderer;
import lombok.val;
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.gui.DrawableHelper;
import v1_8_9.net.minecraft.entity.effect.StatusEffect;
import v1_8_9.net.minecraft.util.Identifier;

public class PotionEffectRenderer implements IPotionEffectRenderer, Initializer {

    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }
    
    @Override
    public void render(PotionType type, double x, double y, double scale) {
        val id = switch (type) {
            case SPEED -> 1;
            case SLOWNESS -> 2;
            case HASTE -> 3;
            case MINING_FATIGUE -> 4;
            case STRENGTH -> 5;
            case INSTANT_HEALTH -> 6;
            case INSTANT_DAMAGE -> 7;
            case JUMP_BOOST -> 8;
            case NAUSEA -> 9;
            case REGENERATION -> 10;
            case RESISTANCE -> 11;
            case FIRE_RESISTANCE -> 12;
            case WATER_BREATHING -> 13;
            case INVISIBILITY -> 14;
            case BLINDNESS -> 15;
            case NIGHT_VISION -> 16;
            case HUNGER -> 17;
            case WEAKNESS -> 18;
            case POISON -> 19;
            case WITHER -> 20;
            case HEALTH_BOOST -> 21;
            case ABSORPTION -> 22;
            case SATURATION -> 23;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        val index = StatusEffect.STATUS_EFFECTS[id].method_2444();
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/container/inventory.png"));
        GlStateManager.enableTexture();
        GlStateManager.enableBlend();
        GlStateManager.color4f(1f, 1f, 1f, 1f);

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, 0.0);
        GlStateManager.scaled(scale, scale, 1.0);
        new DrawableHelper().drawTexture(0, 0, index % 8 * 18, 198 + index / 8 * 18, 18, 18);
        GlStateManager.popMatrix();
    }
    
}
