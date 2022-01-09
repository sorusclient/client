package com.github.sorusclient.client.adapter.v1_8_9.event;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.event.*;
import com.github.sorusclient.client.adapter.v1_8_9.Util;
import com.github.sorusclient.client.event.EventManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.util.Window;
import v1_8_9.net.minecraft.client.world.ClientWorld;
import v1_8_9.net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import v1_8_9.net.minecraft.util.math.Box;

public class EventHook {

    public static void onRender() {
        Sorus.getInstance().get(EventManager.class).call(new RenderEvent());
        GL11.glEnable(GL11.GL_TEXTURE_2D);
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

    public static ArmorBarRenderEvent onArmorBarRender() {
        ArmorBarRenderEvent event = new ArmorBarRenderEvent();
        Sorus.getInstance().get(EventManager.class).call(event);
        return event;
    }

    public static BossBarRenderEvent onBossBarRender() {
        BossBarRenderEvent event = new BossBarRenderEvent();
        Sorus.getInstance().get(EventManager.class).call(event);
        return event;
    }

    public static ExperienceBarRenderEvent onExperienceBarRender() {
        ExperienceBarRenderEvent event = new ExperienceBarRenderEvent();
        Sorus.getInstance().get(EventManager.class).call(event);
        return event;
    }

    public static HealthBarRenderEvent onHealthBarRender() {
        HealthBarRenderEvent event = new HealthBarRenderEvent();
        Sorus.getInstance().get(EventManager.class).call(event);
        return event;
    }

    public static HotBarRenderEvent onHotBarRender() {
        HotBarRenderEvent event = new HotBarRenderEvent();
        Sorus.getInstance().get(EventManager.class).call(event);
        return event;
    }

    public static HungerBarRenderEvent onHungerBarRender() {
        HungerBarRenderEvent event = new HungerBarRenderEvent();
        Sorus.getInstance().get(EventManager.class).call(event);
        return event;
    }

    public static SideBarRenderEvent onSideBarRender() {
        SideBarRenderEvent event = new SideBarRenderEvent();
        Sorus.getInstance().get(EventManager.class).call(event);
        return event;
    }

    public static BlockOutlineRenderEvent onBlockOutlineRender(Box box) {
        BlockOutlineRenderEvent event = new BlockOutlineRenderEvent(new com.github.sorusclient.client.adapter.Box(box.minX, box.maxX, box.minY, box.maxY, box.minZ, box.maxZ));
        Sorus.getInstance().get(EventManager.class).call(event);
        return event;
    }

}
