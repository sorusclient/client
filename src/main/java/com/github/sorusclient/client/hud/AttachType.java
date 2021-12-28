package com.github.sorusclient.client.hud;

import com.github.sorusclient.client.util.Axis;

public class AttachType {

    private final double selfSide, otherSide;
    private final Axis axis;

    private AttachType() {
        this(0, 0, Axis.X);
    }

    public AttachType(double selfSide, double otherSide, Axis axis) {
        this.selfSide = selfSide;
        this.otherSide = otherSide;
        this.axis = axis;
    }

    public double getSelfSide() {
        return selfSide;
    }

    public double getOtherSide() {
        return otherSide;
    }

    public Axis getAxis() {
        return axis;
    }

    public AttachType reverse() {
        return new AttachType(this.otherSide, this.selfSide, this.axis);
    }

}
