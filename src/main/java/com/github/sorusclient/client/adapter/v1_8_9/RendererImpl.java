package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IRenderer;
import com.github.sorusclient.client.adapter.Point;
import com.github.sorusclient.client.adapter.RenderBuffer;
import com.github.sorusclient.client.adapter.Vertex;
import com.github.sorusclient.client.adapter.IFontRenderer;
import com.github.sorusclient.client.util.Color;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.render.BufferBuilder;
import v1_8_9.net.minecraft.client.render.Tessellator;
import v1_8_9.net.minecraft.client.render.VertexFormats;
import v1_8_9.net.minecraft.client.texture.Texture;
import v1_8_9.net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public void drawRectangle(double x, double y, double width, double height, double cornerRadius, Color topLeftColor, Color bottomLeftColor, Color bottomRightColor, Color topRightColor) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_POLYGON, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(x + cornerRadius, y + height, 0).color((float) bottomLeftColor.getRed(), (float) bottomLeftColor.getGreen(), (float) bottomLeftColor.getBlue(), (float) bottomLeftColor.getAlpha()).next();
        bufferBuilder.vertex(x + width - cornerRadius, y + height, 0).color((float) bottomRightColor.getRed(), (float) bottomRightColor.getGreen(), (float) bottomRightColor.getBlue(), (float) bottomRightColor.getAlpha()).next();

        for (int i = 0; i < 90; i++) {
            bufferBuilder.vertex(x + width - cornerRadius + Math.sin(Math.toRadians(i)) * cornerRadius, y + height - cornerRadius + Math.cos(Math.toRadians(i)) * cornerRadius, 0).color((float) bottomRightColor.getRed(), (float) bottomRightColor.getGreen(), (float) bottomRightColor.getBlue(), (float) bottomRightColor.getAlpha()).next();
        }

        bufferBuilder.vertex(x + width, y + height - cornerRadius, 0).color((float) bottomRightColor.getRed(), (float) bottomRightColor.getGreen(), (float) bottomRightColor.getBlue(), (float) bottomRightColor.getAlpha()).next();
        bufferBuilder.vertex(x + width, y + cornerRadius, 0).color((float) topRightColor.getRed(), (float) topRightColor.getGreen(), (float) topRightColor.getBlue(), (float) topRightColor.getAlpha()).next();

        for (int i = 90; i < 180; i++) {
            bufferBuilder.vertex(x + width - cornerRadius + Math.sin(Math.toRadians(i)) * cornerRadius, y + cornerRadius + Math.cos(Math.toRadians(i)) * cornerRadius, 0).color((float) topRightColor.getRed(), (float) topRightColor.getGreen(), (float) topRightColor.getBlue(), (float) topRightColor.getAlpha()).next();
        }

        bufferBuilder.vertex(x + width - cornerRadius, y, 0).color((float) topRightColor.getRed(), (float) topRightColor.getGreen(), (float) topRightColor.getBlue(), (float) topRightColor.getAlpha()).next();
        bufferBuilder.vertex(x + cornerRadius, y, 0).color((float) topLeftColor.getRed(), (float) topLeftColor.getGreen(), (float) topLeftColor.getBlue(), (float) topLeftColor.getAlpha()).next();

        for (int i = 180; i < 270; i++) {
            bufferBuilder.vertex(x + cornerRadius + Math.sin(Math.toRadians(i)) * cornerRadius, y + cornerRadius + Math.cos(Math.toRadians(i)) * cornerRadius, 0).color((float) topLeftColor.getRed(), (float) topLeftColor.getGreen(), (float) topLeftColor.getBlue(), (float) topLeftColor.getAlpha()).next();
        }

        bufferBuilder.vertex(x, y + cornerRadius, 0).color((float) topLeftColor.getRed(), (float) topLeftColor.getGreen(), (float) topLeftColor.getBlue(), (float) topLeftColor.getAlpha()).next();
        bufferBuilder.vertex(x, y + height - cornerRadius, 0).color((float) bottomLeftColor.getRed(), (float) bottomLeftColor.getGreen(), (float) bottomLeftColor.getBlue(), (float) bottomLeftColor.getAlpha()).next();

        for (int i = 270; i < 360; i++) {
            bufferBuilder.vertex(x + cornerRadius + Math.sin(Math.toRadians(i)) * cornerRadius, y + height - cornerRadius + Math.cos(Math.toRadians(i)) * cornerRadius, 0).color((float) bottomLeftColor.getRed(), (float) bottomLeftColor.getGreen(), (float) bottomLeftColor.getBlue(), (float) bottomLeftColor.getAlpha()).next();
        }

        tessellator.draw();
    }

    private final Map<String, Integer> textureMap = new HashMap<>();

    //TODO: rounded images, probably could be done with just calculating x and y texture positions
    @Override
    public void drawImage(String imagePath, double x, double y, double width, double height, Color color) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier(imagePath));

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(x, y + height, 0).texture(0, 1).next();
        bufferBuilder.vertex(x + width, y + height, 0).texture(1, 1).next();
        bufferBuilder.vertex(x + width, y, 0).texture(1, 0).next();
        bufferBuilder.vertex(x, y, 0).texture(0, 0).next();
        tessellator.draw();
    }

    private final Map<String, IFontRenderer> fontRenderers = new HashMap<>();

    @Override
    public IFontRenderer getFontRenderer(String id) {
        IFontRenderer fontRenderer = this.fontRenderers.get(id);

        if (fontRenderer != null) {
            return fontRenderer;
        }

        switch (id) {
            case "minecraft":
                fontRenderer = new MinecraftFontRenderer(MinecraftClient.getInstance().textRenderer);
                this.fontRenderers.put(id, fontRenderer);
                return fontRenderer;
            default:
                return null;
        }
    }

}
