package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IRenderer;
import com.github.sorusclient.client.adapter.Point;
import com.github.sorusclient.client.adapter.RenderBuffer;
import com.github.sorusclient.client.adapter.Vertex;
import com.github.sorusclient.client.util.Color;
import org.lwjgl.opengl.GL11;
import v1_8_9.net.minecraft.client.render.BufferBuilder;
import v1_8_9.net.minecraft.client.render.Tessellator;
import v1_8_9.net.minecraft.client.render.VertexFormats;

public class RendererImpl implements IRenderer {

    @Override
    public void draw(RenderBuffer buffer) {
        int mode;
        switch (buffer.getDrawMode()) {
            case QUAD:
                mode = GL11.GL_QUADS;
                break;
            default:
                mode = -1;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(mode, VertexFormats.POSITION_COLOR);

        for (Vertex vertex : buffer.getVertices()) {
            Point point = vertex.getPoint();
            Color color = vertex.getColor();
            bufferBuilder.vertex(point.getX(), point.getY(), point.getZ()).color((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getAlpha()).next();
        }

        tessellator.draw();
    }

    @Override
    public void setColor(Color color) {
        GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    @Override
    public void setLineThickness(double thickness) {
        GL11.glLineWidth((float) thickness);
    }

}
