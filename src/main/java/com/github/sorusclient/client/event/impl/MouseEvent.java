package com.github.sorusclient.client.event.impl;

import com.github.sorusclient.client.adapter.Button;
import com.github.sorusclient.client.adapter.Key;
import com.github.sorusclient.client.event.Event;

public class MouseEvent extends Event {

    private final Button button;
    private final boolean pressed;
    private final double x, y;

    public MouseEvent(Button button, boolean pressed, double x, double y) {
        this.button = button;
        this.pressed = pressed;
        this.x = x;
        this.y = y;
    }

    public Button getButton() {
        return this.button;
    }

    public boolean isPressed() {
        return this.pressed;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

}
