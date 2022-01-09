package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IFontRenderer;
import com.github.sorusclient.client.util.Color;
import org.lwjgl.opengl.GL11;
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.font.TextRenderer;
import v1_8_9.net.minecraft.util.Identifier;

public class MinecraftFontRenderer implements IFontRenderer {

    private final TextRenderer textRenderer;

    public MinecraftFontRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    @Override
    public void drawString(String text, double x, double y, double scale, Color color) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);

        textRenderer.draw(text, 0, 0, color.getRgb(), false);

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glPopMatrix();
    }

    @Override
    public double getWidth(String text) {
        return textRenderer.getStringWidth(text) - 1;
    }

    @Override
    public double getHeight() {
        return textRenderer.fontHeight - 1;
    }

}
