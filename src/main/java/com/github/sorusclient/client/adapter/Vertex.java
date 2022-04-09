/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter;

import com.github.sorusclient.client.util.Color;

public class Vertex {

    private Point point;
    private Color color;

    public Point getPoint() {
        return point;
    }

    public Color getColor() {
        return color;
    }

    public Vertex setPoint(Point point) {
        this.point = point;
        return this;
    }

    public Vertex setColor(Color color) {
        this.color = color;
        return this;
    }

}
