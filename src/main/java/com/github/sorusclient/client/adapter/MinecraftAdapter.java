package com.github.sorusclient.client.adapter;

import com.github.glassmc.loader.GlassLoader;

public class MinecraftAdapter {

    private IAdapter getAdapter() {
        return GlassLoader.getInstance().getInterface(IAdapter.class);
    }

    public ScreenType getOpenScreen() {
        return this.getAdapter().getOpenScreen();
    }

    public double[] getScreenDimensions() {
        return this.getAdapter().getScreenDimensions();
    }

    public double[] getMouseLocation() {
        return this.getAdapter().getMouseLocation();
    }

    public IPlayerEntity getPlayer() {
        return this.getAdapter().getPlayer();
    }

    public IWorld getWorld() {
        return this.getAdapter().getWorld();
    }

    public void openScreen(ScreenType screenType) {
        this.getAdapter().openScreen(screenType);
    }

}
