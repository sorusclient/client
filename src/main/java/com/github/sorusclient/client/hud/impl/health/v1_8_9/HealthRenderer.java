package com.github.sorusclient.client.hud.impl.health.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.sorusclient.client.hud.impl.health.IHealthRenderer;
import org.lwjgl.opengl.GL11;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.gui.DrawableHelper;
import v1_8_9.net.minecraft.util.Identifier;

public class HealthRenderer implements Listener, IHealthRenderer {

    @Override
    public void run() {
        GlassLoader.getInstance().registerInterface(IHealthRenderer.class, this);
    }

    @Override
    public void renderHeart(double x, double y, double scale, HeartType heartType, HeartRenderType heartRenderType) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);

        DrawableHelper drawableHelper = new DrawableHelper();
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);

        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/icons.png"));

        int xLocation = heartType == HeartType.HEALTH ? 52 : 52 + 12 * 9;

        if (heartRenderType == HeartRenderType.FULL) {
            drawableHelper.drawTexture(0, 0, xLocation, 0, 9, 9);
        } else if (heartRenderType == HeartRenderType.HALF_EMPTY) {
            drawableHelper.drawTexture(0, 0, xLocation + 9, 0, 9, 9);
        } else if (heartRenderType == HeartRenderType.HALF_DAMAGE) {
            drawableHelper.drawTexture(0, 0, xLocation + 18, 0, 9, 9);
            drawableHelper.drawTexture(0, 0, xLocation + 9, 0, 9, 9);
        } else if (heartRenderType == HeartRenderType.DAMAGE) {
            drawableHelper.drawTexture(0, 0, xLocation + 18, 0, 9, 9);
        } else if (heartRenderType == HeartRenderType.DAMAGE_EMPTY) {
            drawableHelper.drawTexture(0, 0, xLocation + 27, 0, 9, 9);
        }

        GL11.glPopMatrix();
    }

    @Override
    public void renderHeartBackground(double x, double y, double scale, BackgroundType backgroundType) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);

        DrawableHelper drawableHelper = new DrawableHelper();
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);

        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/icons.png"));

        switch (backgroundType) {
            case STANDARD:
                drawableHelper.drawTexture(0, 0, 16, 0, 9, 9);
                break;
            case FLASHING_OUTLINE:
                drawableHelper.drawTexture(0, 0, 25, 0, 9, 9);
                break;
        }
        GL11.glPopMatrix();
    }

}
