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
            case 10:
                return PotionType.REGENERATION;
            case 12:
                return PotionType.FIRE_RESISTANCE;
            case 22:
                return PotionType.ABSORPTION;
            default:
                return PotionType.UNKNOWN;
        }
    }

}
