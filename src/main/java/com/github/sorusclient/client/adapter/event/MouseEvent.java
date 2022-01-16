package com.github.sorusclient.client.adapter.event;

import com.github.sorusclient.client.adapter.Button;
import com.github.sorusclient.client.event.Event;

public class MouseEvent extends Event {

    private final Button button;
    private final boolean pressed;
    private final double x, y;
    private final double wheel;

    public MouseEvent(Button button, boolean pressed, double x, double y, double wheel) {
        this.button = button;
        this.pressed = pressed;
        this.x = x;
        this.y = y;
        this.wheel = wheel;
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

    public double getWheel() {
        return wheel;
    }

}
