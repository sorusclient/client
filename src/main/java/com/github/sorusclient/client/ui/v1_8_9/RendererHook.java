package com.github.sorusclient.client.ui.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.sorusclient.client.ui.IFontRenderer;
import com.github.sorusclient.client.ui.IRenderer;
import com.github.sorusclient.client.util.Color;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.render.BufferBuilder;
import v1_8_9.net.minecraft.client.render.Tessellator;
import v1_8_9.net.minecraft.client.render.VertexFormats;
import v1_8_9.net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RendererHook implements Listener, IRenderer {

    @Override
    public void run() {
        GlassLoader.getInstance().registerInterface(IRenderer.class, this);
    }

    @Override
    public void drawRectangle(double x, double y, double width, double height, double cornerRadius, Color topLeftColor, Color bottomLeftColor, Color bottomRightColor, Color topRightColor) {
        boolean textureEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);

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

        if (textureEnabled) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        } else {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }

        if (blendEnabled) {
            GL11.glEnable(GL11.GL_BLEND);
        } else {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    private final Map<String, Integer> textureMap = new HashMap<>();

    //TODO: rounded images, probably could be done with just calculating x and y texture positions
    @Override
    public void drawImage(String imagePath, double x, double y, double width, double height, Color color) {
        int id = this.textureMap.getOrDefault(imagePath, -1);
        if (id == -1) {
            try {
                id = GL11.glGenTextures();

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

                BufferedImage image = ImageIO.read(Objects.requireNonNull(RendererHook.class.getClassLoader().getResourceAsStream(imagePath)));
                int[] pixels = new int[image.getWidth() * image.getHeight()];
                image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

                ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

                for(int pixelY = 0; pixelY < image.getHeight(); pixelY++){
                    for(int pixelX = 0; pixelX < image.getWidth(); pixelX++){
                        int pixel = pixels[pixelY * image.getWidth() + pixelX];
                        buffer.put((byte) -1);
                        buffer.put((byte) -1);
                        buffer.put((byte) -1);
                        buffer.put((byte) ((pixel >> 24) & 0xFF));
                    }
                }

                buffer.flip();

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

                this.textureMap.put(imagePath, id);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

        boolean textureEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);

        float red = (float) color.getRed();
        float green = (float) color.getGreen();
        float blue = (float) color.getBlue();
        float alpha = (float) color.getAlpha();
        GlStateManager.color4f(red, green, blue, alpha);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(x, y + height, 0).texture(0, 1).next();
        bufferBuilder.vertex(x + width, y + height, 0).texture(1, 1).next();
        bufferBuilder.vertex(x + width, y, 0).texture(1, 0).next();
        bufferBuilder.vertex(x, y, 0).texture(0, 0).next();
        tessellator.draw();

        if (textureEnabled) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        } else {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }

        if (blendEnabled) {
            GL11.glEnable(GL11.GL_BLEND);
        } else {
            GL11.glDisable(GL11.GL_BLEND);
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
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
