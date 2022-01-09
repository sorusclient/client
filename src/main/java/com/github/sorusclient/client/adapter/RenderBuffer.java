package com.github.sorusclient.client.adapter;

import java.util.ArrayList;
import java.util.List;

public class RenderBuffer {

    private DrawMode drawMode;
    private final List<Vertex> vertices = new ArrayList<>();

    public void setDrawMode(DrawMode drawMode) {
        this.drawMode = drawMode;
    }

    public RenderBuffer push(Vertex vertex) {
        this.vertices.add(vertex);
        return this;
    }

    public DrawMode getDrawMode() {
        return drawMode;
    }

    public List<Vertex> getVertices() {
        return this.vertices;
    }

    public enum DrawMode {
        QUAD
    }

}