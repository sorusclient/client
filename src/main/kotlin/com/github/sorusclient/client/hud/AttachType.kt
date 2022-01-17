package com.github.sorusclient.client.hud

import com.github.sorusclient.client.util.Axis

class AttachType(val selfSide: Double, val otherSide: Double, val axis: Axis) {

    private constructor() : this(0.0, 0.0, Axis.X)

    fun reverse(): AttachType {
        return AttachType(otherSide, selfSide, axis)
    }

}