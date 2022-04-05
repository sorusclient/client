/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud

import com.github.sorusclient.client.util.Axis

class AttachType(val selfSide: Double, val otherSide: Double, val axis: Axis) {

    private constructor() : this(0.0, 0.0, Axis.X)

    fun reverse(): AttachType {
        return AttachType(otherSide, selfSide, axis)
    }

}