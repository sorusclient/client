package com.github.sorusclient.client.module.impl.blockoverlay.v1_8_9;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.module.ModuleManager;
import com.github.sorusclient.client.module.impl.blockoverlay.BlockOverlay;
import com.github.sorusclient.client.util.Color;
import org.lwjgl.opengl.GL11;
import v1_8_9.net.minecraft.client.render.BufferBuilder;
import v1_8_9.net.minecraft.client.render.Tessellator;
import v1_8_9.net.minecraft.client.render.VertexFormats;
import v1_8_9.net.minecraft.util.math.Box;

public class BlockOverlayHook {

    public static void preRenderOutline(Box box) {
        BlockOverlay blockOverlay = Sorus.getInstance().get(ModuleManager.class).get(BlockOverlay.class);
        if (blockOverlay.isEnabled()) {
            Color borderColor = blockOverlay.getBorderColor();
            GL11.glColor4d(borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), borderColor.getAlpha());

            GL11.glLineWidth((float) blockOverlay.getBorderThickness());

            Color fillColor = blockOverlay.getFillColor();
            float red = (float) fillColor.getRed();
            float green = (float) fillColor.getGreen();
            float blue = (float) fillColor.getBlue();
            float alpha = (float) fillColor.getAlpha();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(box.minX, box.minY, box.minZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.minX, box.maxY, box.minZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.maxX, box.minY, box.minZ).color(red, green, blue, alpha).next();

            bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.minX, box.minY, box.maxZ).color(red, green, blue, alpha).next();

            bufferBuilder.vertex(box.maxX, box.minY, box.minZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).color(red, green, blue, alpha).next();

            bufferBuilder.vertex(box.minX, box.minY, box.maxZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.minX, box.maxY, box.minZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.minX, box.minY, box.minZ).color(red, green, blue, alpha).next();

            bufferBuilder.vertex(box.maxX, box.maxY, box.minZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.minX, box.maxY, box.minZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.minX, box.maxY, box.maxZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.maxX, box.maxY, box.maxZ).color(red, green, blue, alpha).next();

            bufferBuilder.vertex(box.maxX, box.minY, box.maxZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.minX, box.minY, box.maxZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.minX, box.minY, box.minZ).color(red, green, blue, alpha).next();
            bufferBuilder.vertex(box.maxX, box.minY, box.minZ).color(red, green, blue, alpha).next();

            tessellator.draw();
        }
    }

}
