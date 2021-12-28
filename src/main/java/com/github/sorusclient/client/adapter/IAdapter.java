package com.github.sorusclient.client.adapter;

public interface IAdapter {

    ScreenType getOpenScreen();

    double[] getScreenDimensions();

    double[] getMouseLocation();

    IPlayerEntity getPlayer();

    IWorld getWorld();

    void openScreen(ScreenType screenType);

}
