package com.github.sorusclient.client.hud.impl.hunger.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.sorusclient.client.hud.impl.hunger.IHungerRenderer;
import org.lwjgl.opengl.GL11;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.gui.DrawableHelper;
import v1_8_9.net.minecraft.util.Identifier;

public class HungerRenderer implements Listener, IHungerRenderer {

    @Override
    public void run() {
        GlassLoader.getInstance().registerInterface(IHungerRenderer.class, this);
    }

    @Override
    public void renderHunger(double x, double y, double scale, HeartRenderType heartRenderType) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);

        DrawableHelper drawableHelper = new DrawableHelper();
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);

        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/icons.png"));

        int xLocation = 52;

        if (heartRenderType == HeartRenderType.FULL) {
            drawableHelper.drawTexture(0, 0, xLocation, 27, 9, 9);
        } else if (heartRenderType == HeartRenderType.HALF) {
            drawableHelper.drawTexture(0, 0, xLocation + 9, 27, 9, 9);
        }

        GL11.glPopMatrix();
    }

    @Override
    public void renderHungerBackground(double x, double y, double scale) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);

        DrawableHelper drawableHelper = new DrawableHelper();
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);

        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/icons.png"));

        drawableHelper.drawTexture(0, 0, 16, 27, 9, 9);

        GL11.glPopMatrix();
    }

}
