package com.github.sorusclient.client.adapter;

public interface IPotionEffect {

    String getDuration();
    String getName();
    int getAmplifier();
    PotionType getType();

    enum PotionType {
        SPEED,
        SLOWNESS,
        HASTE,
        MINING_FATIGUE,
        STRENGTH,
        INSTANT_HEALTH,
        INSTANT_DAMAGE,
        JUMP_BOOST,
        NAUSEA,
        REGENERATION,
        RESISTANCE,
        FIRE_RESISTANCE,
        WATER_BREATHING,
        INVISIBILITY,
        BLINDNESS,
        NIGHT_VISION,
        HUNGER,
        WEAKNESS,
        POISON,
        WITHER,
        HEALTH_BOOST,
        ABSORPTION,
        SATURATION,
        UNKNOWN
    }

}
