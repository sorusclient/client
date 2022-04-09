/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class RenderBuffer {

    private DrawMode drawMode;
    private final List<Vertex> vertices = new ArrayList<>();

    public void push(Vertex vertex) {
        vertices.add(vertex);
    }

    public void setDrawMode(DrawMode drawMode) {
        this.drawMode = drawMode;
    }

    public DrawMode getDrawMode() {
        return drawMode;
    }

    public List<Vertex> getVertices() {
        return ImmutableList.copyOf(vertices);
    }

    public enum DrawMode {
        QUAD
    }

}
