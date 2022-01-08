package com.github.sorusclient.client.adapter.event;

import com.github.sorusclient.client.event.Event;
import com.github.sorusclient.client.adapter.Key;

public class KeyEvent extends Event {

    private final Key key;
    private final char character;
    private final boolean pressed;
    private final boolean repeat;

    public KeyEvent(Key key, char character, boolean pressed, boolean repeat) {
        this.key = key;
        this.character = character;
        this.pressed = pressed;
        this.repeat = repeat;
    }

    public Key getKey() {
        return key;
    }

    public char getCharacter() {
        return character;
    }

    public boolean isPressed() {
        return pressed;
    }

    public boolean isRepeat() {
        return repeat;
    }

}
