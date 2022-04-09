/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2;

import com.github.sorusclient.client.IdentifierKt;
import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.adapter.*;
import com.github.sorusclient.client.bootstrap.Initializer;
import v1_18_2.net.minecraft.client.MinecraftClient;
import v1_18_2.net.minecraft.client.gui.screen.*;
import v1_18_2.net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import v1_18_2.net.minecraft.client.gui.screen.option.OptionsScreen;
import v1_18_2.net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import v1_18_2.net.minecraft.client.network.ServerAddress;
import v1_18_2.net.minecraft.client.network.ServerInfo;
import v1_18_2.net.minecraft.client.option.Perspective;
import v1_18_2.net.minecraft.text.LiteralText;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class AdapterImpl implements IAdapter, Initializer {

    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }

    @Override
    public ScreenType getOpenScreen() {
        return Util.INSTANCE.screenToScreenType(MinecraftClient.getInstance().currentScreen);
    }

    @Override
    public void openScreen(ScreenType screenType) {
        var screen = switch (screenType) {
            case DUMMY -> new DummyScreen();
            case SETTINGS -> createSettingsScreen();
            case CONTROLS -> new ControlsOptionsScreen(createSettingsScreen(), MinecraftClient.getInstance().options);
            case VIDEO_SETTINGS -> new VideoOptionsScreen(createSettingsScreen(), MinecraftClient.getInstance().options);
            default -> throw new IllegalStateException("Unexpected value: " + screenType);
        };

        MinecraftClient.getInstance().setScreen(screen);
    }

    private OptionsScreen createSettingsScreen() {
        var inGame = MinecraftClient.getInstance().world != null;
        return new OptionsScreen(inGame ? new GameMenuScreen(true) : new TitleScreen(), MinecraftClient.getInstance().options);
    }

    @Override
    public double[] getScreenDimensions() {
        var window = MinecraftClient.getInstance().getWindow();
        return new double[] {window.getScaledWidth(), window.getScaledHeight()};
    }

    @Override
    public double[] getMouseLocation() {
        var mouse = MinecraftClient.getInstance().mouse;
        var window = MinecraftClient.getInstance().getWindow();
        return new double[] {mouse.getX() / window.getWidth() * window.getScaledWidth(), mouse.getY() / window.getHeight() * window.getScaledHeight()};
    }

    @Override
    public IPlayerEntity getPlayer() {
        var player = MinecraftClient.getInstance().player;
        return player != null ? new PlayerEntityImpl(player) : null;
    }

    @Override
    public IWorld getWorld() {
        return new WorldImpl(MinecraftClient.getInstance().world);
    }

    @Override
    public PerspectiveMode getPerspective() {
        return switch (MinecraftClient.getInstance().options.getPerspective()) {
            case FIRST_PERSON -> PerspectiveMode.FIRST_PERSON;
            case THIRD_PERSON_BACK -> PerspectiveMode.THIRD_PERSON_BACK;
            case THIRD_PERSON_FRONT -> PerspectiveMode.THIRD_PERSON_FRONT;
            default -> PerspectiveMode.UNKNOWN;
        };
    }

    @Override
    public void setPerspective(PerspectiveMode perspectiveMode) {
        MinecraftClient.getInstance().options.setPerspective(switch (perspectiveMode) {
            case FIRST_PERSON -> Perspective.FIRST_PERSON;
            case THIRD_PERSON_BACK -> Perspective.THIRD_PERSON_BACK;
            case THIRD_PERSON_FRONT -> Perspective.THIRD_PERSON_FRONT;
            default -> throw new IllegalStateException("Unexpected value: " + perspectiveMode);
        });
    }

    @Override
    public IServer getCurrentServer() {
        var serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
        if (serverInfo != null) {
            return new ServerImpl(serverInfo);
        } else {
            return null;
        }
    }

    @Override
    public IKeyBind getKeyBind(KeyBindType keyBindType) {
        var options = MinecraftClient.getInstance().options;
        var keyBinding = switch (keyBindType) {
            case SPRINT -> options.sprintKey;
            case SNEAK -> options.sneakKey;
        };

        return new KeyBindImpl(keyBinding);
    }

    @Override
    public void sendPlayerMessage(String message) {
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().player.sendChatMessage(message);
        } else {
            System.err.println("Attempted to send player message but player was null");
        }
    }

    @Override
    public void setDisplayTitle(String title) {
        MinecraftClient.getInstance().getWindow().setTitle(title);
    }

    @Override
    public void setDisplayIcon(String iconSmall, String iconLarge) {
        MinecraftClient.getInstance().getWindow().setIcon(getInputStream(iconSmall), getInputStream(iconLarge));
    }

    private InputStream getInputStream(String path) {
        return AdapterImpl.class.getClassLoader().getResourceAsStream(path);
    }

    @Override
    public void leaveWorld() {
        if (MinecraftClient.getInstance().world != null) {
            MinecraftClient.getInstance().world.disconnect();
        }
        MinecraftClient.getInstance().setScreen(new TitleScreen());
    }

    @Override
    public void joinServer(String ip) {
        var serverAddress = ServerAddress.parse(ServerAddress.parse(ip).getAddress());
        var serverInfo = new ServerInfo("", ip, false);
        MinecraftClient.getInstance().setCurrentServerEntry(new ServerInfo("", ip, false));
        ConnectScreen.connect(new TitleScreen(), MinecraftClient.getInstance(), serverAddress, serverInfo);
    }

    @Override
    public GameMode getGameMode() {
        return switch (MinecraftClient.getInstance().interactionManager.getCurrentGameMode()) {
            case SURVIVAL -> GameMode.SURVIVAL;
            case CREATIVE -> GameMode.CREATIVE;
            case ADVENTURE -> GameMode.ADVENTURE;
            case SPECTATOR -> GameMode.SPECTATOR;
        };
    }

    @Override
    public ISession getSession() {
        return new SessionImpl();
    }

    @Override
    public IText createText(String string) {
        return Util.INSTANCE.textToApiText(new LiteralText(string));
    }

    @Override
    public String getVersion() {
        return "1.18.2";
    }

    private final IRenderer renderer = new RendererImpl();

    @Override
    public IRenderer getRenderer() {
        return renderer;
    }

    private static final Field currentFPS;

    static {
        var currentFps = IdentifierKt.toIdentifier("v1_18_2/net/minecraft/client/MinecraftClient#currentFps");
        try {
            currentFPS = MinecraftClient.class.getDeclaredField(currentFps.getFieldName());
            currentFPS.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int getFPS() {
        try {
            return currentFPS.getInt(MinecraftClient.getInstance());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

}
