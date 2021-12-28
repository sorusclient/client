package com.github.sorusclient.client.adapter;

public interface IPotionEffect {

    String getDuration();
    String getName();
    int getAmplifier();
    PotionType getType();

    enum PotionType {
        SPEED,
        REGENERATION,
        FIRE_RESISTANCE,
        ABSORPTION,
        UNKNOWN
    }

}
