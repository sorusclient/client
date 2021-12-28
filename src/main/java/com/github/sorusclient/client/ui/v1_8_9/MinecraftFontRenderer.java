package com.github.sorusclient.client.ui.v1_8_9;

import com.github.sorusclient.client.ui.IFontRenderer;
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
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("assets/minecraft/textures/font/ascii.png"));
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);

        GlStateManager.disableDepthTest();
        GlStateManager.enableTexture();
        GlStateManager.enableBlend();

        textRenderer.draw(text, 0, 0, color.getRgb(), false);

        GlStateManager.enableDepthTest();

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
