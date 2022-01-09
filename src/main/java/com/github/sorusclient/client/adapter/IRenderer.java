package com.github.sorusclient.client.adapter;

import com.github.sorusclient.client.util.Color;

public interface IRenderer {

    void draw(RenderBuffer buffer);

    void setColor(Color color);
    void setLineThickness(double thickness);

}
