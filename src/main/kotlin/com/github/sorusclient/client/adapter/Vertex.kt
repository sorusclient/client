/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter

import com.github.sorusclient.client.util.Color

class Vertex {
    lateinit var point: Point
        private set
    lateinit var color: Color
        private set

    fun setPoint(point: Point): Vertex {
        this.point = point
        return this
    }

    fun setColor(color: Color): Vertex {
        this.color = color
        return this
    }
}