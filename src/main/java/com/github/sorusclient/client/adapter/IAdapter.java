package com.github.sorusclient.client.adapter;

public interface IAdapter {

    ScreenType getOpenScreen();

    double[] getScreenDimensions();

    double[] getMouseLocation();

    IPlayerEntity getPlayer();

    IWorld getWorld();

    void openScreen(ScreenType screenType);

    void setPerspective(PerspectiveMode perspectiveMode);
    PerspectiveMode getPerspective();

    IServer getCurrentServer();

    IKeyBind getKeyBind(IKeyBind.KeyBindType type);

    IRenderer getRenderer();

}
