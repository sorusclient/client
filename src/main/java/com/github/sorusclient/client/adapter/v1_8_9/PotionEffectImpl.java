package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IPotionEffect;
import v1_8_9.net.minecraft.client.resource.language.I18n;
import v1_8_9.net.minecraft.entity.effect.StatusEffectInstance;

public class PotionEffectImpl implements IPotionEffect {

    protected final StatusEffectInstance effect;

    public PotionEffectImpl(StatusEffectInstance effect) {
        this.effect = effect;
    }

    @Override
    public String getDuration() {
        if (this.effect.isPermanent()) {
            return "**:**";
        }
        int duration = this.effect.getDuration();
        int seconds = duration / 20;
        int minutes = seconds / 60;
        int secondsReal = (minutes == 0 ? seconds : (seconds % (minutes * 60)));
        return minutes + ":" + (secondsReal < 10 ? "0" : "") + secondsReal;
    }

    @Override
    public String getName() {
        return I18n.translate(this.effect.getTranslationKey());
    }

    @Override
    public int getAmplifier() {
        return this.effect.getAmplifier() + 1;
    }

    @Override
    public PotionType getType() {
        switch (this.effect.getEffectId()) {
            case 1:
                return PotionType.SPEED;
            case 2:
                return PotionType.SLOWNESS;
            case 3:
                return PotionType.HASTE;
            case 4:
                return PotionType.MINING_FATIGUE;
            case 5:
                return PotionType.STRENGTH;
            case 6:
                return PotionType.INSTANT_HEALTH;
            case 7:
                return PotionType.INSTANT_DAMAGE;
            case 8:
                return PotionType.JUMP_BOOST;
            case 9:
                return PotionType.NAUSEA;
            case 10:
                return PotionType.REGENERATION;
            case 11:
                return PotionType.RESISTANCE;
            case 12:
                return PotionType.FIRE_RESISTANCE;
            case 13:
                return PotionType.WATER_BREATHING;
            case 14:
                return PotionType.INVISIBILITY;
            case 15:
                return PotionType.BLINDNESS;
            case 16:
                return PotionType.NIGHT_VISION;
            case 17:
                return PotionType.HUNGER;
            case 18:
                return PotionType.WEAKNESS;
            case 19:
                return PotionType.POISON;
            case 20:
                return PotionType.WITHER;
            case 21:
                return PotionType.HEALTH_BOOST;
            case 22:
                return PotionType.ABSORPTION;
            case 23:
                return PotionType.SATURATION;
            default:
                return PotionType.UNKNOWN;
        }
    }

}
