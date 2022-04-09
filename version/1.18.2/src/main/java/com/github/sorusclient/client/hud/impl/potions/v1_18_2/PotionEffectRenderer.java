/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.potions.v1_18_2;

import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.adapter.v1_18_2.RenderUtil;
import com.github.sorusclient.client.adapter.PotionType;
import com.github.sorusclient.client.bootstrap.Initializer;
import com.github.sorusclient.client.hud.impl.potions.IPotionEffectRenderer;
import lombok.val;
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem;
import v1_18_2.net.minecraft.client.MinecraftClient;
import v1_18_2.net.minecraft.entity.effect.StatusEffects;

public class PotionEffectRenderer implements IPotionEffectRenderer, Initializer {
    
    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }

    @Override
    public void render(PotionType type, double x, double y, double scale) {
        val potion = switch (type) {
            case SPEED -> StatusEffects.SPEED;
            case SLOWNESS -> StatusEffects.SLOWNESS;
            case HASTE -> StatusEffects.HASTE;
            case MINING_FATIGUE -> StatusEffects.MINING_FATIGUE;
            case STRENGTH -> StatusEffects.STRENGTH;
            case INSTANT_HEALTH -> StatusEffects.INSTANT_HEALTH;
            case INSTANT_DAMAGE -> StatusEffects.INSTANT_DAMAGE;
            case JUMP_BOOST -> StatusEffects.JUMP_BOOST;
            case NAUSEA -> StatusEffects.NAUSEA;
            case REGENERATION -> StatusEffects.REGENERATION;
            case RESISTANCE -> StatusEffects.RESISTANCE;
            case FIRE_RESISTANCE -> StatusEffects.FIRE_RESISTANCE;
            case WATER_BREATHING -> StatusEffects.WATER_BREATHING;
            case INVISIBILITY -> StatusEffects.INVISIBILITY;
            case BLINDNESS -> StatusEffects.BLINDNESS;
            case NIGHT_VISION -> StatusEffects.NIGHT_VISION;
            case HUNGER -> StatusEffects.HUNGER;
            case WEAKNESS -> StatusEffects.WEAKNESS;
            case POISON -> StatusEffects.POISON;
            case WITHER -> StatusEffects.WITHER;
            case HEALTH_BOOST -> StatusEffects.HEALTH_BOOST;
            case ABSORPTION -> StatusEffects.ABSORPTION;
            case SATURATION -> StatusEffects.SATURATION;
            case GLOWING -> StatusEffects.GLOWING;
            case LEVITATION -> StatusEffects.LEVITATION;
            case LUCK -> StatusEffects.LUCK;
            case UNLUCK -> StatusEffects.UNLUCK;
            case SLOW_FALLING -> StatusEffects.SLOW_FALLING;
            case CONDUIT_POWER -> StatusEffects.CONDUIT_POWER;
            case DOLPHINS_GRACE -> StatusEffects.DOLPHINS_GRACE;
            case BAD_OMEN -> StatusEffects.BAD_OMEN;
            case HERO_OF_THE_VILLAGE -> StatusEffects.HERO_OF_THE_VILLAGE;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        val statusEffectSpriteManager = MinecraftClient.getInstance().getStatusEffectSpriteManager();
        val sprite = statusEffectSpriteManager.getSprite(potion);

        RenderSystem.enableBlend();
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());

        RenderUtil.drawTexture(x, y, sprite.getMinU() * 256, sprite.getMinV() * 256, 18 * scale, 18 * scale, (sprite.getMaxU() - sprite.getMinU()) * 256, (sprite.getMaxV() - sprite.getMinV()) * 256);
    }
    
}
