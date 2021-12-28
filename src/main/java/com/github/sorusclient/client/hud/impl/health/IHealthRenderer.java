package com.github.sorusclient.client.hud.impl.health;

public interface IHealthRenderer {
    void renderHeart(double x, double y, double scale, HeartType heartType, HeartRenderType heartRenderType);
    void renderHeartBackground(double x, double y, double scale, BackgroundType backgroundType);

    enum HeartType {
        HEALTH,
        ABSORPTION,
    }

    enum HeartRenderType {
        FULL,
        HALF_EMPTY,
        HALF_DAMAGE,
        DAMAGE,
        DAMAGE_EMPTY,
        EMPTY
    }

    enum BackgroundType {
        FLASHING_OUTLINE,
        STANDARD,
    }

}
