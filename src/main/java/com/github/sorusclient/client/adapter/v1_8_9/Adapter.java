package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.sorusclient.client.adapter.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.gui.screen.GameMenuScreen;
import v1_8_9.net.minecraft.client.gui.screen.Screen;
import v1_8_9.net.minecraft.client.util.Window;
import v1_8_9.net.minecraft.entity.Entity;

public class Adapter implements Listener, IAdapter {

    @Override
    public void run() {
        GlassLoader.getInstance().registerInterface(IAdapter.class, new Adapter());
    }

    @Override
    public ScreenType getOpenScreen() {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen instanceof GameMenuScreen) {
            return ScreenType.GAME_MENU;
        } else if (screen instanceof DummyScreen) {
            return ScreenType.DUMMY;
        } else if (screen == null) {
            return ScreenType.IN_GAME;
        } else {
            return ScreenType.UNKNOWN;
        }
    }

    @Override
    public double[] getScreenDimensions() {
        Window window = new Window(MinecraftClient.getInstance());
        return new double[] {window.getScaledWidth(), window.getScaledHeight()};
    }

    @Override
    public double[] getMouseLocation() {
        Window window = new Window(MinecraftClient.getInstance());

        int x = Mouse.getX();
        int y = Mouse.getY();

        return new double[] {
                x / (double) Display.getWidth() * window.getScaledWidth(),
                window.getScaledHeight() - (y / (double) Display.getHeight() * window.getScaledHeight())
        };
    }

    @Override
    public IPlayerEntity getPlayer() {
        Entity player = MinecraftClient.getInstance().player;
        return player != null ? new PlayerEntityImpl(player) : null;
    }

    @Override
    public IWorld getWorld() {
        return new WorldImpl(MinecraftClient.getInstance().world);
    }

    @Override
    public void openScreen(ScreenType screenType) {
        Screen screen = null;
        switch (screenType) {
            case DUMMY:
                screen = new DummyScreen();
                break;
            case IN_GAME:
                break;
        }

        MinecraftClient.getInstance().openScreen(screen);
    }

    @Override
    public void setPerspective(PerspectiveMode perspectiveMode) {
        int newPerspective = -1;
        switch (perspectiveMode) {
            case FIRST_PERSON:
                newPerspective = 0;
                break;
            case THIRD_PERSON_BACK:
                newPerspective = 1;
                break;
            case THIRD_PERSON_FRONT:
                newPerspective = 2;
                break;
        }

        MinecraftClient.getInstance().options.perspective = newPerspective;
    }

    @Override
    public PerspectiveMode getPerspective() {
        switch (MinecraftClient.getInstance().options.perspective) {
            case 0:
                return PerspectiveMode.FIRST_PERSON;
            case 1:
                return PerspectiveMode.THIRD_PERSON_BACK;
            case 2:
                return PerspectiveMode.THIRD_PERSON_FRONT;
        }
        return null;
    }

}
