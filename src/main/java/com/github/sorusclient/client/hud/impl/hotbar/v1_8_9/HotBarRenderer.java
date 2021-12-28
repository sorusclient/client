package com.github.sorusclient.client.hud.impl.hotbar.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.sorusclient.client.adapter.IItem;
import com.github.sorusclient.client.hud.impl.hotbar.IHotBarRenderer;
import org.lwjgl.opengl.GL11;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.gui.DrawableHelper;
import v1_8_9.net.minecraft.client.render.GuiLighting;
import v1_8_9.net.minecraft.item.ItemStack;
import v1_8_9.net.minecraft.util.Identifier;

public class HotBarRenderer implements Listener, IHotBarRenderer {

    @Override
    public void run() {
        GlassLoader.getInstance().registerInterface(IHotBarRenderer.class, this);
    }

    @Override
    public void renderBackground(double x, double y, double scale) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);

        DrawableHelper drawableHelper = new DrawableHelper();

        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/widgets.png"));

        drawableHelper.drawTexture(0, 0, 0, 0, 182, 22);

        GL11.glPopMatrix();
    }

    @Override
    public void renderItem(double x, double y, double scale, IItem item) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);

        GuiLighting.enable();

        MinecraftClient.getInstance().getItemRenderer().renderInGuiWithOverrides((ItemStack) item.getInner(), 0, 0);

        GuiLighting.disable();

        GL11.glPopMatrix();
    }

    @Override
    public void renderSelectedSlot(double x, double y, double scale) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);

        DrawableHelper drawableHelper = new DrawableHelper();

        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/widgets.png"));

        drawableHelper.drawTexture(0, 0, 0, 22, 24, 24);

        GL11.glPopMatrix();
    }

}
