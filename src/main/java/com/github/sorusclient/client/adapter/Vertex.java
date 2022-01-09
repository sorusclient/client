package com.github.sorusclient.client.adapter;

import com.github.sorusclient.client.util.Color;

public class Vertex {

    private Point point;
    private Color color;

    public Vertex setPoint(Point point) {
        this.point = point;
        return this;
    }

    public Vertex setColor(Color color) {
        this.color = color;
        return this;
    }

    public Point getPoint() {
        return point;
    }

    public Color getColor() {
        return color;
    }

}