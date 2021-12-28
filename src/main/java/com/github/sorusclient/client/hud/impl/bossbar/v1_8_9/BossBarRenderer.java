package com.github.sorusclient.client.hud.impl.bossbar.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.sorusclient.client.hud.impl.bossbar.IBossBarRenderer;
import org.lwjgl.opengl.GL11;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.gui.DrawableHelper;
import v1_8_9.net.minecraft.util.Identifier;

public class BossBarRenderer implements Listener, IBossBarRenderer {

    @Override
    public void run() {
        GlassLoader.getInstance().registerInterface(IBossBarRenderer.class, this);
    }

    @Override
    public void renderBossBar(double x, double y, double scale, double percent) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);

        DrawableHelper drawableHelper = new DrawableHelper();
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);

        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/icons.png"));

        drawableHelper.drawTexture(0, 0, 0, 74, 183, 5);
        drawableHelper.drawTexture(0, 0, 0, 79, (int) (183 * percent), 5);

        GL11.glPopMatrix();
    }

}
