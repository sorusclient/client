package com.github.sorusclient.client.adapter.v1_8_9.event;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.Button;
import com.github.sorusclient.client.adapter.event.*;
import com.github.sorusclient.client.adapter.v1_8_9.Util;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.adapter.Key;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.util.Window;
import v1_8_9.net.minecraft.client.world.ClientWorld;
import v1_8_9.net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;

public class EventHook {

    public static void onRender() {
        Sorus.getInstance().get(EventManager.class).call(new RenderEvent());
    }

    public static void onInGameRender() {
        Sorus.getInstance().get(EventManager.class).call(new RenderInGameEvent());
    }

    public static void onKey() {
        int key = Keyboard.getEventKey();
        boolean pressed = Keyboard.getEventKeyState();
        boolean repeat = Keyboard.isRepeatEvent();

        Sorus.getInstance().get(EventManager.class).call(new KeyEvent(Util.getKey(key), Keyboard.getEventCharacter(), pressed, repeat));
    }

    public static void onMouse() {
        int button = Mouse.getEventButton();
        boolean pressed = Mouse.getEventButtonState();

        int x = Mouse.getEventX();
        int y = Mouse.getEventY();

        Window window = new Window(MinecraftClient.getInstance());

        Sorus.getInstance().get(EventManager.class).call(new MouseEvent(
                Util.getButton(button),
                pressed,
                x / (double) Display.getWidth() * window.getScaledWidth(),
                window.getScaledHeight() - (y / (double) Display.getHeight() * window.getScaledHeight())));
    }

    public static void onConnect(ClientWorld world, String ip) {
        if (world == null && ip.isEmpty()) {
            Sorus.getInstance().get(EventManager.class).call(new GameLeaveEvent());
        }
    }

    public static void onCustomPayload(CustomPayloadS2CPacket packet) {
        String channel = packet.getChannel();
        if (channel.startsWith("sorus:")) {
            channel = channel.substring(6);
            Sorus.getInstance().get(EventManager.class).call(new SorusCustomPacketEvent(channel, packet.getPayload().readString(32767)));
        }
    }

    public static void onGameJoin() {
        Sorus.getInstance().get(EventManager.class).call(new GameJoinEvent());
    }

}
