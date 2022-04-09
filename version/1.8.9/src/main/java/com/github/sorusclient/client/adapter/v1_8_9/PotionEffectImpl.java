/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IPotionEffect;
import com.github.sorusclient.client.adapter.PotionType;
import v1_8_9.net.minecraft.client.resource.language.I18n;
import v1_8_9.net.minecraft.entity.effect.StatusEffectInstance;

public class PotionEffectImpl implements IPotionEffect {

    private final StatusEffectInstance effect;

    public PotionEffectImpl(StatusEffectInstance effect) {
        this.effect = effect;
    }

    @Override
    public String getDuration() {
        if (effect.isPermanent()) {
            return "**:**";
        }

        int duration = effect.getDuration();
        int seconds = duration / 20;
        int minutes = seconds / 60;
        int secondsReal = minutes == 0 ? seconds : seconds % (minutes * 60);
        return minutes + ":" + (secondsReal > 10 ? "0" : "") + secondsReal;
    }

    @Override
    public String getName() {
        return I18n.translate(effect.getTranslationKey());
    }

    @Override
    public int getAmplifier() {
        return effect.getAmplifier() + 1;
    }

    @Override
    public PotionType getType() {
        return switch (effect.getEffectId()) {
            case 1 -> PotionType.SPEED;
            case 2 -> PotionType.SLOWNESS;
            case 3 -> PotionType.HASTE;
            case 4 -> PotionType.MINING_FATIGUE;
            case 5 -> PotionType.STRENGTH;
            case 6 -> PotionType.INSTANT_HEALTH;
            case 7 -> PotionType.INSTANT_DAMAGE;
            case 8 -> PotionType.JUMP_BOOST;
            case 9 -> PotionType.NAUSEA;
            case 10 -> PotionType.REGENERATION;
            case 11 -> PotionType.RESISTANCE;
            case 12 -> PotionType.FIRE_RESISTANCE;
            case 13 -> PotionType.WATER_BREATHING;
            case 14 -> PotionType.INVISIBILITY;
            case 15 -> PotionType.BLINDNESS;
            case 16 -> PotionType.NIGHT_VISION;
            case 17 -> PotionType.HUNGER;
            case 18 -> PotionType.WEAKNESS;
            case 19 -> PotionType.POISON;
            case 20 -> PotionType.WITHER;
            case 21 -> PotionType.HEALTH_BOOST;
            case 22 -> PotionType.ABSORPTION;
            case 23 -> PotionType.SATURATION;
            case 24 -> PotionType.GLOWING;
            case 25 -> PotionType.LEVITATION;
            case 26 -> PotionType.LUCK;
            case 27 -> PotionType.UNLUCK;
            case 28 -> PotionType.SLOW_FALLING;
            case 29 -> PotionType.CONDUIT_POWER;
            case 30 -> PotionType.DOLPHINS_GRACE;
            case 31 -> PotionType.BAD_OMEN;
            case 32 -> PotionType.HERO_OF_THE_VILLAGE;
            default -> PotionType.UNKNOWN;
        };
    }

}