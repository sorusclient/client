/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter;

public interface IAdapter {
    ScreenType getOpenScreen();
    void openScreen(ScreenType screenType);
    double[] getScreenDimensions();
    double[] getMouseLocation();

    IPlayerEntity getPlayer();
    IWorld getWorld();

    PerspectiveMode getPerspective();
    void setPerspective(PerspectiveMode perspectiveMode);

    IServer getCurrentServer();

    IKeyBind getKeyBind(KeyBindType keyBindType);

    void sendPlayerMessage(String message);

    void setDisplayTitle(String title);
    void setDisplayIcon(String iconSmall, String iconLarge);

    void leaveWorld();
    void joinServer(String ip);

    GameMode getGameMode();

    ISession getSession();

    IText createText(String string);

    String getVersion();

    IRenderer getRenderer();

    int getFPS();

}
