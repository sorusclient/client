/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.InterfaceManager;
import com.github.sorusclient.client.adapter.*;
import com.github.sorusclient.client.bootstrap.Initializer;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.gui.screen.*;
import v1_8_9.net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import v1_8_9.net.minecraft.client.network.ServerInfo;
import v1_8_9.net.minecraft.client.util.Window;
import v1_8_9.net.minecraft.network.ServerAddress;
import v1_8_9.net.minecraft.text.LiteralText;
import v1_8_9.org.lwjgl.input.Mouse;
import v1_8_9.org.lwjgl.opengl.Display;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AdapterImpl implements IAdapter, Initializer {

    @Override
    public void initialize() {
        InterfaceManager.register(this);
    }

    @Override
    public ScreenType getOpenScreen() {
        return Util.screenToScreenType(MinecraftClient.getInstance().currentScreen);
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

        MinecraftClient.getInstance().openScreen(screen);
    }

    private SettingsScreen createSettingsScreen() {
        var inGame = MinecraftClient.getInstance().world != null;
        return new SettingsScreen(inGame ? new GameMenuScreen() : new TitleScreen(), MinecraftClient.getInstance().options);
    }

    @Override
    public double[] getScreenDimensions() {
        var window = new Window(MinecraftClient.getInstance());
        return new double[] {window.getScaledWidth(), window.getScaledHeight()};
    }

    @Override
    public double[] getMouseLocation() {
        var window = new Window(MinecraftClient.getInstance());
        var x = Mouse.getX();
        var y = Mouse.getY();
        return new double[] {x / (double) Display.getWidth() * window.getScaledWidth(), window.getScaledHeight() - y / (double) Display.getHeight() * window.getScaledHeight()};
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
        return switch (MinecraftClient.getInstance().options.perspective) {
            case 0 -> PerspectiveMode.FIRST_PERSON;
            case 1 -> PerspectiveMode.THIRD_PERSON_BACK;
            case 2 -> PerspectiveMode.THIRD_PERSON_FRONT;
            default -> PerspectiveMode.UNKNOWN;
        };
    }

    @Override
    public void setPerspective(PerspectiveMode perspectiveMode) {
        MinecraftClient.getInstance().options.perspective = switch (perspectiveMode) {
            case FIRST_PERSON -> 0;
            case THIRD_PERSON_BACK -> 1;
            case THIRD_PERSON_FRONT -> 2;
            default -> throw new IllegalStateException("Unexpected value: " + perspectiveMode);
        };
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
            case SPRINT -> options.keySprint;
            case SNEAK -> options.keySneak;
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
        Display.setTitle(title);
    }

    @Override
    public void setDisplayIcon(String iconSmall, String iconLarge) {
        Display.setIcon(new ByteBuffer[] {getByteBuffer(iconSmall), getByteBuffer(iconLarge)});
    }

    private ByteBuffer getByteBuffer(String path) {
        try {
            BufferedImage bufferedImage = ImageIO.read(AdapterImpl.class.getClassLoader().getResourceAsStream(path));

            var buffer = ByteBuffer.allocateDirect(bufferedImage.getWidth() * bufferedImage.getHeight() * 4);
            var rgba = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
            bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), rgba, 0, bufferedImage.getWidth());

            for (var pixelY = 0; pixelY < bufferedImage.getHeight(); pixelY++) {
                for (var pixelX = 0; pixelX < bufferedImage.getWidth(); pixelX++) {
                    var rgb = rgba[bufferedImage.getWidth() * pixelY + pixelX];
                    var red = rgb >> 16 & 0xFF;
                    var green = rgb >> 8 & 0xFF;
                    var blue = rgb & 0xFF;
                    var alpha = rgb >> 24 & 0xFF;

                    buffer.put((byte) red);
                    buffer.put((byte) green);
                    buffer.put((byte) blue);
                    buffer.put((byte) alpha);
                }
            }

            buffer.flip();

            return buffer;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    @Override
    public void leaveWorld() {
        if (MinecraftClient.getInstance().world != null) {
            MinecraftClient.getInstance().world.disconnect();
        }
        MinecraftClient.getInstance().connect(null);
    }

    @Override
    public void joinServer(String ip) {
        var serverAddress = ServerAddress.parse(ServerAddress.parse(ip).getAddress());
        MinecraftClient.getInstance().setCurrentServerEntry(new ServerInfo("", ip, false));
        MinecraftClient.getInstance().openScreen(new ConnectScreen(new TitleScreen(), MinecraftClient.getInstance(), serverAddress.getAddress(), serverAddress.getPort()));
    }

    @Override
    public GameMode getGameMode() {
        return switch (MinecraftClient.getInstance().interactionManager.getCurrentGameMode()) {
            case SURVIVAL -> GameMode.SURVIVAL;
            case CREATIVE -> GameMode.CREATIVE;
            case ADVENTURE -> GameMode.ADVENTURE;
            case SPECTATOR -> GameMode.SPECTATOR;
            default -> throw new IllegalStateException("Unexpected value: " + MinecraftClient.getInstance().interactionManager.getCurrentGameMode());
        };
    }

    @Override
    public ISession getSession() {
        return new SessionImpl();
    }

    @Override
    public IText createText(String string) {
        return Util.textToApiText(new LiteralText(string));
    }

    @Override
    public String getVersion() {
        return "1.8.9";
    }

    private final IRenderer renderer = new RendererImpl();

    @Override
    public IRenderer getRenderer() {
        return renderer;
    }

    @Override
    public int getFPS() {
        return MinecraftClient.getCurrentFps();
    }

}
