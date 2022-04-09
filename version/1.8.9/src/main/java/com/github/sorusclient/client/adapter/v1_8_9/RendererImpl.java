/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.IdentifierKt;
import com.github.sorusclient.client.adapter.IFontRenderer;
import com.github.sorusclient.client.adapter.IRenderer;
import com.github.sorusclient.client.adapter.RenderBuffer;
import com.github.sorusclient.client.util.Color;
import org.apache.commons.io.IOUtils;
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.render.GameRenderer;
import v1_8_9.net.minecraft.client.render.Tessellator;
import v1_8_9.net.minecraft.client.render.VertexFormats;
import v1_8_9.net.minecraft.client.util.Window;
import v1_8_9.org.lwjgl.opengl.GL11;
import v1_8_9.org.lwjgl.opengl.GL15;
import v1_8_9.org.lwjgl.opengl.GL20;
import v1_8_9.org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RendererImpl implements IRenderer {

    @Override
    public void draw(RenderBuffer buffer) {
        var mode = switch (buffer.getDrawMode()) {
            case QUAD -> GL11.GL_QUADS;
        };

        var tessellator = Tessellator.getInstance();
        var bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(mode, VertexFormats.POSITION_COLOR);

        for (var vertex : buffer.getVertices()) {
            var point = vertex.getPoint();
            var color = vertex.getColor();
            bufferBuilder.vertex(point.x(), point.y(), point.z()).color((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getAlpha()).next();
        }
        tessellator.draw();
    }

    @Override
    public void setColor(Color color) {
        GL11.glColor4f((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getAlpha());
    }

    private boolean createdPrograms = false;

    private int imageProgram = 0;
    private int imageVao = 0;

    private int roundedRectangleProgram = 0;
    private int roundedRectangleVao = 0;

    private int roundedRectangleBorderProgram = 0;
    private int roundedRectangleBorderVao = 0;

    private int rectangleColoredProgram = 0;
    private int rectangleColoredVao = 0;

    private int createProgram(String vertexShaderPath, String fragmentShaderPath) {
        var program = GL20.glCreateProgram();

        var vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        try {
            GL20.glShaderSource(vertexShader, IOUtils.toString(RendererImpl.class.getClassLoader().getResourceAsStream(vertexShaderPath), StandardCharsets.UTF_8));
            GL20.glCompileShader(vertexShader);
            var compiled = GL20.glGetShaderi(vertexShader, GL20.GL_COMPILE_STATUS);
            if (compiled == 0) {
                System.err.println(GL20.glGetShaderInfoLog(vertexShader, GL20.glGetShaderi(vertexShader, GL20.GL_INFO_LOG_LENGTH)));
                throw new IllegalStateException("Failed to compile shader");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        GL20.glAttachShader(program, vertexShader);

        var fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        try {
            GL20.glShaderSource(fragmentShader, IOUtils.toString(RendererImpl.class.getClassLoader().getResourceAsStream(fragmentShaderPath), StandardCharsets.UTF_8));
            GL20.glCompileShader(fragmentShader);
            var compiled = GL20.glGetShaderi(fragmentShader, GL20.GL_COMPILE_STATUS);
            if (compiled == 0) {
                System.err.println(GL20.glGetShaderInfoLog(fragmentShader, GL20.glGetShaderi(fragmentShader, GL20.GL_INFO_LOG_LENGTH)));
                throw new IllegalStateException("Failed to compile shader");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        GL20.glAttachShader(program, fragmentShader);
        GL20.glLinkProgram(program);

        var linked = GL20.glGetProgrami(program, GL20.GL_LINK_STATUS);
        if (linked == 0) {
            System.err.println(GL20.glGetProgramInfoLog(program, GL20.glGetProgrami(program, GL20.GL_INFO_LOG_LENGTH)));
            throw new IllegalStateException("Shader failed to link");
        }
        return program;
    }

    private void createPrograms() {
        if (createdPrograms) return;

        createdPrograms = true;

        roundedRectangleProgram = createProgram("rounded_rectangle_vertex.glsl", "rectangle_fragment.glsl");
        {
            roundedRectangleVao = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(roundedRectangleVao);
            var vertices = new float[] {
                    1f, 1f,
                    1f, 0f,
                    0f, 0f,
                    0f, 0f,
                    0f, 1f,
                    1f, 1f
            };

            var verticesBuffer = ByteBuffer.allocateDirect(vertices.length << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
            verticesBuffer.put(vertices).flip();

            var vboID = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
            GL30.glBindVertexArray(0);
        }

        roundedRectangleBorderProgram = createProgram("rectangle_border_vertex.glsl", "rounded_rectangle_border_fragment.glsl");
        {
            roundedRectangleBorderVao = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(roundedRectangleBorderVao);
            var vertices = new float[]{
                    1f, 1f,
                    1f, 0f,
                    0f, 0f,
                    0f, 0f,
                    0f, 1f,
                    1f, 1f
            };

            var verticesBuffer = ByteBuffer.allocateDirect(vertices.length << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
            verticesBuffer.put(vertices).flip();

            var vboID = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
            GL30.glBindVertexArray(0);
        }

        imageProgram = createProgram("image_vertex.glsl", "image_fragment.glsl");
        {
            imageVao = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(imageVao);
            var vertices = new float[]{
                    1f, 1f,
                    1f, 0f,
                    0f, 0f,
                    0f, 0f,
                    0f, 1f,
                    1f, 1f
            };
            
            var verticesBuffer = ByteBuffer.allocateDirect(vertices.length << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
            verticesBuffer.put(vertices).flip();

            var vboID = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
            GL30.glBindVertexArray(0);
        }

        rectangleColoredProgram = createProgram("colored_rectangle_vertex.glsl", "colored_rectangle_fragment.glsl");
        {
            rectangleColoredVao = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(rectangleColoredVao);
            var vertices = new float[]{
                    1f, 1f,
                    1f, 0f,
                    0f, 0f,
                    0f, 0f,
                    0f, 1f,
                    1f, 1f
            };

            var verticesBuffer = ByteBuffer.allocateDirect(vertices.length << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
            verticesBuffer.put(vertices).flip();

            var vboID = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
            GL30.glBindVertexArray(0);
        }
    }

    @Override
    public void drawRectangle(double x, double y, double width, double height, double cornerRadius, Color topLeftColor, Color bottomLeftColor, Color bottomRightColor, Color topRightColor) {
        if (topLeftColor.getRgb() == topRightColor.getRgb() && topRightColor.getRgb() == bottomRightColor.getRgb()) {
            this.createPrograms();

            GlStateManager.enableBlend();
            GlStateManager.disableTexture();

            GL20.glUseProgram(roundedRectangleProgram);

            GL30.glBindVertexArray(roundedRectangleVao);
            GL20.glEnableVertexAttribArray(0);

            var window = new Window(MinecraftClient.getInstance());

            GL20.glUniform4f(GL20.glGetUniformLocation(roundedRectangleProgram, "position1"), (float) x, (float) y, (float) width, (float) height);
            GL20.glUniform4f(GL20.glGetUniformLocation(roundedRectangleProgram, "colorIn"), (float) topLeftColor.getRed(), (float) topLeftColor.getGreen(), (float) topLeftColor.getBlue(), (float) topLeftColor.getAlpha());
            GL20.glUniform2f(GL20.glGetUniformLocation(roundedRectangleProgram, "resolutionIn"), (float) window.getScaledWidth(), (float) window.getScaledHeight());
            GL20.glUniform1f(GL20.glGetUniformLocation(roundedRectangleProgram, "cornerRadiusIn"), (float) cornerRadius);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

            GL20.glDisableVertexAttribArray(0);

            GL20.glUseProgram(0);

            GlStateManager.disableBlend();
            GlStateManager.enableTexture();
        } else {
            this.createPrograms();

            GlStateManager.enableBlend();
            GlStateManager.disableTexture();

            GL20.glUseProgram(rectangleColoredProgram);

            GL30.glBindVertexArray(rectangleColoredVao);
            GL20.glEnableVertexAttribArray(0);

            var window = new Window(MinecraftClient.getInstance());

            GL20.glUniform4f(GL20.glGetUniformLocation(rectangleColoredProgram, "position1"), (float) x, (float) y, (float) width, (float) height);
            GL20.glUniform4f(GL20.glGetUniformLocation(rectangleColoredProgram, "colorIn1"), (float) topLeftColor.getRed(), (float) topLeftColor.getGreen(), (float) topLeftColor.getBlue(), (float) topLeftColor.getAlpha());
            GL20.glUniform4f(GL20.glGetUniformLocation(rectangleColoredProgram, "colorIn2"), (float) topRightColor.getRed(), (float) topRightColor.getGreen(), (float) topRightColor.getBlue(), (float) topRightColor.getAlpha());
            GL20.glUniform4f(GL20.glGetUniformLocation(rectangleColoredProgram, "colorIn3"), (float) bottomRightColor.getRed(), (float) bottomRightColor.getGreen(), (float) bottomRightColor.getBlue(), (float) bottomRightColor.getAlpha());
            GL20.glUniform4f(GL20.glGetUniformLocation(rectangleColoredProgram, "colorIn4"), (float) bottomLeftColor.getRed(), (float) bottomLeftColor.getGreen(), (float) bottomLeftColor.getBlue(), (float) bottomLeftColor.getAlpha());
            GL20.glUniform2f(GL20.glGetUniformLocation(rectangleColoredProgram, "resolutionIn"), (float) window.getScaledWidth(), (float) window.getScaledHeight());

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

            GL20.glDisableVertexAttribArray(0);

            GlStateManager.disableBlend();
            GlStateManager.enableTexture();
        }

        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL20.glUseProgram(0);
    }

    @Override
    public void drawRectangleBorder(double x, double y, double width, double height, double cornerRadius, double thickness, Color color) {
        createPrograms();

        GlStateManager.enableBlend();

        GL20.glUseProgram(roundedRectangleBorderProgram);
        GL30.glBindVertexArray(roundedRectangleBorderVao);
        GL20.glEnableVertexAttribArray(0);

        var window = new Window(MinecraftClient.getInstance());

        GL20.glUniform4f(GL20.glGetUniformLocation(roundedRectangleBorderProgram, "position1"), (float) x, (float) y, (float) width, (float) height);
        GL20.glUniform4f(GL20.glGetUniformLocation(roundedRectangleBorderProgram, "colorIn"), (float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getAlpha());
        GL20.glUniform2f(GL20.glGetUniformLocation(roundedRectangleBorderProgram, "resolutionIn"), (float) window.getScaledWidth(), (float) window.getScaledHeight());
        GL20.glUniform1f(GL20.glGetUniformLocation(roundedRectangleBorderProgram, "cornerRadiusIn"), (float) cornerRadius);
        GL20.glUniform1f(GL20.glGetUniformLocation(roundedRectangleBorderProgram, "thicknessIn"), (float) thickness);

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL20.glUseProgram(0);
    }

    private final Map<String, Integer> textures = new HashMap<>();

    public int getTexture(String id) {
        var texture = textures.getOrDefault(id, -1);
        if (texture == -1) {
            createTexture(id);
            texture = textures.getOrDefault(id, -1);
        }
        return texture;
    }

    private int setupTexture(byte[] bytes, boolean antialias) {
        var glId = -1;

        try {
            var bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
            glId = GL11.glGenTextures();
            GlStateManager.bindTexture(glId);

            int filter1;
            int filter2;
            if (antialias) {
                filter1 = GL11.GL_LINEAR;
                filter2 = GL11.GL_LINEAR_MIPMAP_LINEAR;
            } else {
                filter1 = GL11.GL_NEAREST;
                filter2 = GL11.GL_NEAREST_MIPMAP_NEAREST;
            }

            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, (float) filter1);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, (float) filter2);

            var buffer = ByteBuffer.allocateDirect(bufferedImage.getWidth() * bufferedImage.getHeight() * 4);
            var rgba = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
            bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), rgba, 0, bufferedImage.getWidth());

            for (var pixelY = 0; pixelY < bufferedImage.getHeight(); pixelY++) {
                for (var pixelX = 0; pixelX < bufferedImage.getWidth(); pixelX++) {
                    var rgb = rgba[bufferedImage.getWidth() * pixelY + pixelX];
                    var red = rgb >> 16 & 0xFF;
                    var green = rgb >> 8 & 0xFF;
                    var blue = rgb & 0xFF;
                    var alpha = rgb >> 24 & 0xFF;

                    if (red == 0 && green == 0 && blue == 0 && alpha == 0) {
                        red = 255;
                        green = 255;
                        blue = 255;
                    }

                    buffer.put((byte) red);
                    buffer.put((byte) green);
                    buffer.put((byte) blue);
                    buffer.put((byte) alpha);
                }
            }
            buffer.flip();

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, bufferedImage.getWidth(), bufferedImage.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return glId;
    }

    @Override
    public void drawImage(String id, double x, double y, double width, double height, double cornerRadius, double textureX, double textureY, double textureWidth, double textureHeight, boolean antialias, Color color) {
        var glId = getTexture(id);
        if (glId == -1) return;

        GlStateManager.bindTexture(glId);
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();

        createPrograms();

        GL20.glUseProgram(imageProgram);
        GL30.glBindVertexArray(imageVao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        var window = new Window(MinecraftClient.getInstance());

        GL20.glUniform4f(GL20.glGetUniformLocation(imageProgram, "position1"), (float) x, (float) y, (float) width, (float) height);
        GL20.glUniform4f(GL20.glGetUniformLocation(imageProgram, "colorIn"), (float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getAlpha());
        GL20.glUniform2f(GL20.glGetUniformLocation(imageProgram, "resolutionIn"), (float) window.getScaledWidth(), (float) window.getScaledHeight());
        GL20.glUniform1f(GL20.glGetUniformLocation(imageProgram, "cornerRadiusIn"), (float) cornerRadius);
        GL20.glUniform4f(GL20.glGetUniformLocation(imageProgram, "imagePositionIn"), (float) textureX, (float) textureY, (float) textureWidth, (float) textureHeight);

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL20.glUseProgram(0);
        GlStateManager.bindTexture(0);
    }

    @Override
    public void createTexture(String id, byte[] bytes, boolean antialias) {
        if (this.textures.getOrDefault(id, -1) != -1) return;
        var texture = setupTexture(bytes, antialias);
        this.textures.put(id, texture);
    }

    @Override
    public void scissor(double x, double y, double width, double height) {
        var minecraft = MinecraftClient.getInstance();
        var window = new Window(minecraft);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                (int) (x * minecraft.width / window.getScaledWidth()),
                (int) ((window.getScaledHeight() - (y + height)) * minecraft.height / window.getScaledHeight()),
                (int) (width * minecraft.width / window.getScaledWidth()),
                (int) (height * minecraft.height / window.getScaledHeight())
        );
    }

    @Override
    public void endScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private final Map<String, IFontRenderer> fontRenderers = new HashMap<>();

    @Override
    public IFontRenderer getFontRenderer(String id) {
        var fontRenderer = fontRenderers.get(id);

        if (fontRenderer == null) {
            switch (id) {
                case "minecraft" -> {
                    fontRenderer = new MinecraftFontRenderer(MinecraftClient.getInstance().textRenderer);
                    fontRenderers.put(id, fontRenderer);
                }
            }
        }

        return fontRenderer;
    }

    private final Map<String, FontData> fonts = new HashMap<>();

    private FontData getFont(String id) {
        var fontData = fonts.get(id);
        if (fontData == null) {
            createFont(id);
            fontData = fonts.get(id);
        }
        return fontData;
    }

    @Override
    public void drawText(String id, String text, double x, double y, double scale, Color color) {
        var fontData = getFont(id);

        GlStateManager.bindTexture(fontData.glId);
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();

        createPrograms();

        GL20.glUseProgram(imageProgram);
        GL30.glBindVertexArray(imageVao);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        var window = new Window(MinecraftClient.getInstance());
        var factor = 200 * (float) scale;

        var xOffset = 0.0;
        for (var character : text.toCharArray()) {
            GL20.glUniform4f(GL20.glGetUniformLocation(imageProgram, "position1"), (float) (xOffset + x), (float) y, (float) fontData.characterData[character].textureWidth * factor, (float) fontData.characterData[character].textureHeight * factor);
            GL20.glUniform4f(GL20.glGetUniformLocation(imageProgram, "colorIn"), (float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getAlpha());
            GL20.glUniform2f(GL20.glGetUniformLocation(imageProgram, "resolutionIn"), (float) window.getScaledWidth(), (float) window.getScaledHeight());
            GL20.glUniform1f(GL20.glGetUniformLocation(imageProgram, "cornerRadiusIn"), 0);
            GL20.glUniform4f(GL20.glGetUniformLocation(imageProgram, "imagePositionIn"), (float) fontData.characterData[character].textureX, (float) fontData.characterData[character].textureY, (float) fontData.characterData[character].textureWidth, (float) fontData.characterData[character].textureHeight);
            xOffset += fontData.characterData[character].textureWidth * factor;
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        }

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL20.glUseProgram(0);
    }

    @Override
    public double getTextWidth(String fontId, String text) {
        var fontData = fonts.get(fontId);

        var width = 0.0;
        var factor = 200.0;
        for (var character : text.toCharArray()) {
            width += fontData.characterData[character].textureWidth * factor;
        }

        return width;
    }

    @Override
    public double getTextHeight(String fontId) {
        var fontData = fonts.get(fontId);
        var factor = 200.0;
        return fontData.ascent * factor;
    }

    @Override
    public void createFont(String id, InputStream inputStream) {
        if (fonts.get(id) != null) return;
        var fontData = setupFont(inputStream);
        fonts.put(id, fontData);
    }

    private FontData setupFont(InputStream inputStream) {
        var fontData = new FontData();
        try {
            var font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            font = font.deriveFont(200f);

            var bufferedImage = new BufferedImage(4096, 4096, BufferedImage.TYPE_INT_ARGB);
            var graphics = (Graphics2D) bufferedImage.getGraphics();

            graphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
            graphics.setColor(new java.awt.Color(255, 255, 255, 0));
            graphics.drawRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
            graphics.setFont(font);

            var fontMetrics = graphics.getFontMetrics();
            graphics.setColor(new java.awt.Color(255, 255, 255, 255));

            var textX = 0;
            var textY = 0;
            var maxHeight = 0;

            fontData.characterData = new FontData.CharacterData[255];

            fontData.ascent = (double) fontMetrics.getAscent() / bufferedImage.getHeight() * 9 / 12;

            for (var i = 0; i < 254; i++) {
                var character = (char) i;
                var weirdAscent = (int) ((double) fontMetrics.getAscent() * 9 / 12);
                var bounds = fontMetrics.getStringBounds(String.valueOf(character), graphics);

                graphics.drawString(String.valueOf(character), textX, textY + weirdAscent);

                var characterData = new FontData.CharacterData();
                characterData.textureX = (double) textX / bufferedImage.getWidth();
                characterData.textureY = (double) textY / bufferedImage.getHeight();
                characterData.textureWidth = bounds.getWidth() / bufferedImage.getWidth();
                characterData.textureHeight = bounds.getHeight() / bufferedImage.getHeight();

                fontData.characterData[i] = characterData;

                textX += (int) (bounds.getWidth() + 15);
                maxHeight = (int) Math.max(maxHeight, bounds.getHeight() * 6 / 5);
                if (textX > 4096 - 300) {
                    textX = 15;
                    textY += maxHeight;
                    maxHeight = 0;
                }
            }

            fontData.glId = GL11.glGenTextures();

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontData.glId);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);

            var buffer = ByteBuffer.allocateDirect(bufferedImage.getWidth() * bufferedImage.getHeight() * 4);
            var rgba = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
            bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), rgba, 0, bufferedImage.getWidth());

            for (var pixelY = 0; pixelY < bufferedImage.getHeight(); pixelY++) {
                for (var pixelX = 0; pixelX < bufferedImage.getWidth(); pixelX++) {
                    var rgb = rgba[bufferedImage.getWidth() * pixelY + pixelX];
                    var red = rgb >> 16 & 0xFF;
                    var green = rgb >> 8 & 0xFF;
                    var blue = rgb & 0xFF;
                    var alpha = rgb >> 24 & 0xFF;

                    if (red == 0 && green == 0 && blue == 0 && alpha == 0) {
                        red = 255;
                        green = 255;
                        blue = 255;
                    }

                    buffer.put((byte) red);
                    buffer.put((byte) green);
                    buffer.put((byte) blue);
                    buffer.put((byte) alpha);
                }
            }
            buffer.flip();

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, bufferedImage.getWidth(), bufferedImage.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        } catch (IOException | FontFormatException e) {
            throw new IllegalStateException(e);
        }
        return fontData;
    }

    private static final Method loadShader;

    static {
        var loadShaderIdentifier = IdentifierKt.toIdentifier("v1_8_9/net/minecraft/client/render/GameRenderer#loadShader(Lv1_8_9/net/minecraft/util/Identifier;)V");
        try {
            loadShader = GameRenderer.class.getDeclaredMethod(loadShaderIdentifier.getMethodName(), v1_8_9.net.minecraft.util.Identifier.class);
            loadShader.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void loadBlur() {
        try {
            loadShader.invoke(MinecraftClient.getInstance().gameRenderer, new v1_8_9.net.minecraft.util.Identifier("sorus/shaders/blur.json"));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void unloadBlur() {
        MinecraftClient.getInstance().gameRenderer.disableShader();
    }

    static class FontData {
        int glId = 0;
        CharacterData[] characterData;
        double ascent;

        static class CharacterData {
            double textureX = 0.0;
            double textureY = 0.0;
            double textureWidth = 0.0;
            double textureHeight = 0.0;
        }
    }

}
