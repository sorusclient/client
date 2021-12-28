package com.github.sorusclient.client.hud.impl.hunger;

import com.github.glassmc.loader.GlassLoader;
import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.IPlayerEntity;
import com.github.sorusclient.client.adapter.MinecraftAdapter;
import com.github.sorusclient.client.hud.HUDElement;

public class Hunger extends HUDElement {

    public Hunger() {
        super("hunger");
    }

    @Override
    protected void render(double x, double y, double scale) {
        IPlayerEntity player = Sorus.getInstance().get(MinecraftAdapter.class).getPlayer();
        if (player == null) return;

        int hunger = (int) player.getHunger();

        IHungerRenderer hungerRenderer = GlassLoader.getInstance().getInterface(IHungerRenderer.class);

        for (int i = 0; i < 10; i++) {
            double heartX = x + (1 + i * 8) * scale;

            hungerRenderer.renderHungerBackground(heartX, y + 1 * scale, scale);

            if (((10 - i) * 2) - 1 < hunger) {
                hungerRenderer.renderHunger(heartX, y + 1 * scale, scale, IHungerRenderer.HeartRenderType.FULL);
            } else if (((10 - i) * 2) - 2 < hunger) {
                hungerRenderer.renderHunger(heartX, y + 1 * scale, scale, IHungerRenderer.HeartRenderType.HALF);
            } else {
                hungerRenderer.renderHunger(heartX, y + 1 * scale, scale, IHungerRenderer.HeartRenderType.EMPTY);
            }
        }
    }

    @Override
    public double getWidth() {
        return 1 + 8 * 10 + 1 + 1;
    }

    @Override
    public double getHeight() {
        return 11;
    }

}
