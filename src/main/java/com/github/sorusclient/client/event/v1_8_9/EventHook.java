package com.github.sorusclient.client.event.v1_8_9;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.adapter.Button;
import com.github.sorusclient.client.event.EventManager;
import com.github.sorusclient.client.event.impl.KeyEvent;
import com.github.sorusclient.client.event.impl.MouseEvent;
import com.github.sorusclient.client.event.impl.RenderEvent;
import com.github.sorusclient.client.adapter.Key;
import com.github.sorusclient.client.event.impl.RenderInGameEvent;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.util.Window;

public class EventHook {

    public static void onRender() {
        Sorus.getInstance().get(EventManager.class).call(new RenderEvent());
    }

    public static void onInGameRender() {
        Sorus.getInstance().get(EventManager.class).call(new RenderInGameEvent());
    }

    private static final BiMap<Integer, Key> keyMap = HashBiMap.create();

    static {
        keyMap.put(1, Key.ESCAPE);
        keyMap.put(16, Key.Q);
        keyMap.put(17, Key.W);
        keyMap.put(18, Key.E);
        keyMap.put(19, Key.R);
        keyMap.put(20, Key.T);
        keyMap.put(21, Key.Y);
        keyMap.put(22, Key.U);
        keyMap.put(23, Key.I);
        keyMap.put(24, Key.O);
        keyMap.put(25, Key.P);
        keyMap.put(30, Key.A);
        keyMap.put(31, Key.S);
        keyMap.put(32, Key.D);
        keyMap.put(33, Key.F);
        keyMap.put(34, Key.G);
        keyMap.put(35, Key.H);
        keyMap.put(36, Key.J);
        keyMap.put(37, Key.K);
        keyMap.put(38, Key.L);
        keyMap.put(44, Key.Z);
        keyMap.put(45, Key.X);
        keyMap.put(46, Key.C);
        keyMap.put(47, Key.V);
        keyMap.put(48, Key.B);
        keyMap.put(49, Key.N);
        keyMap.put(50, Key.M);
    }

    private static final BiMap<Integer, Button> buttonMap = HashBiMap.create();

    static {
        buttonMap.put(0, Button.PRIMARY);
    }

    public static void onKey() {
        int key = Keyboard.getEventKey();
        boolean pressed = Keyboard.getEventKeyState();
        boolean repeat = Keyboard.isRepeatEvent();

        Sorus.getInstance().get(EventManager.class).call(new KeyEvent(keyMap.getOrDefault(key, Key.UNKNOWN), Keyboard.getEventCharacter(), pressed, repeat));
    }

    public static void onMouse() {
        int button = Mouse.getEventButton();
        boolean pressed = Mouse.getEventButtonState();

        int x = Mouse.getEventX();
        int y = Mouse.getEventY();

        Window window = new Window(MinecraftClient.getInstance());

        Sorus.getInstance().get(EventManager.class).call(new MouseEvent(
                buttonMap.getOrDefault(button, Button.UNKNOWN),
                pressed,
                x / (double) Display.getWidth() * window.getScaledWidth(),
                window.getScaledHeight() - (y / (double) Display.getHeight() * window.getScaledHeight())));
    }

}
