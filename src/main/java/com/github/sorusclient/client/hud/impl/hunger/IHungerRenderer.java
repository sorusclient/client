package com.github.sorusclient.client.hud.impl.hunger;

public interface IHungerRenderer {
    void renderHunger(double x, double y, double scale, HeartRenderType heartRenderType);
    void renderHungerBackground(double x, double y, double scale);

    enum HeartRenderType {
        FULL,
        HALF,
        EMPTY,
    }

}
