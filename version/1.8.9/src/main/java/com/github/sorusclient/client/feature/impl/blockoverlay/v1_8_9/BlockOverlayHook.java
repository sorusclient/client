/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.blockoverlay.v1_8_9;

import com.github.sorusclient.client.adapter.AdapterManager;
import com.github.sorusclient.client.adapter.IRenderer;
import com.github.sorusclient.client.adapter.Point;
import com.github.sorusclient.client.adapter.RenderBuffer;
import com.github.sorusclient.client.adapter.Vertex;
import com.github.sorusclient.client.adapter.RenderBuffer.DrawMode;
import com.github.sorusclient.client.feature.impl.blockoverlay.BlockOverlay;
import com.github.sorusclient.client.util.Color;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import v1_8_9.net.minecraft.util.math.Box;
import v1_8_9.org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
public class BlockOverlayHook {

    public static void onBlockOverlayRender(@NotNull Box box) {
        Intrinsics.checkNotNullParameter(box, "box");
        com.github.sorusclient.client.adapter.Box box2 = new com.github.sorusclient.client.adapter.Box(box.minX, box.maxX, box.minY, box.maxY, box.minZ, box.maxZ);
        RenderBuffer buffer = new RenderBuffer();
        buffer.setDrawMode(DrawMode.QUAD);
        Color fillColor = (Color)BlockOverlay.INSTANCE.getFillColor().getValue();
        buffer.push((new Vertex()).setPoint(new Point(box2.minX(), box2.minY(), box2.minZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.minX(), box2.maxY(), box2.minZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.maxX(), box2.maxY(), box2.minZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.maxX(), box2.minY(), box2.minZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.maxX(), box2.minY(), box2.maxZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.maxX(), box2.maxY(), box2.maxZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.minX(), box2.maxY(), box2.maxZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.minX(), box2.minY(), box2.maxZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.maxX(), box2.minY(), box2.minZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.maxX(), box2.maxY(), box2.minZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.maxX(), box2.maxY(), box2.maxZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.maxX(), box2.minY(), box2.maxZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.minX(), box2.minY(), box2.maxZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.minX(), box2.maxY(), box2.maxZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.minX(), box2.maxY(), box2.minZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.minX(), box2.minY(), box2.minZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.maxX(), box2.maxY(), box2.minZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.minX(), box2.maxY(), box2.minZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.minX(), box2.maxY(), box2.maxZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.maxX(), box2.maxY(), box2.maxZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.maxX(), box2.minY(), box2.maxZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.minX(), box2.minY(), box2.maxZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.minX(), box2.minY(), box2.minZ())).setColor(fillColor));
        buffer.push((new Vertex()).setPoint(new Point(box2.maxX(), box2.minY(), box2.minZ())).setColor(fillColor));
        IRenderer renderer = AdapterManager.getAdapter().getRenderer();
        renderer.draw(buffer);
        GL11.glLineWidth((float)((Number)BlockOverlay.INSTANCE.getBorderThickness().getValue()).doubleValue());
        Color borderColor = (Color)BlockOverlay.INSTANCE.getBorderColor().getValue();
        GL11.glColor4f((float)borderColor.getRed(), (float)borderColor.getGreen(), (float)borderColor.getBlue(), (float)borderColor.getAlpha());
    }

}
