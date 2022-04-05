/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter

class RenderBuffer {
    var drawMode: DrawMode? = null
    val vertices: MutableList<Vertex> = ArrayList()

    fun push(vertex: Vertex): RenderBuffer {
        vertices.add(vertex)
        return this
    }

    enum class DrawMode {
        QUAD
    }
}