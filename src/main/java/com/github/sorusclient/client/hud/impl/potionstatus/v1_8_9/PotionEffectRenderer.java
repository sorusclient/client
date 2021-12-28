package com.github.sorusclient.client.hud.impl.potionstatus.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.sorusclient.client.adapter.IPotionEffect;
import com.github.sorusclient.client.hud.impl.potionstatus.IPotionEffectRenderer;
import org.lwjgl.opengl.GL11;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.gui.DrawableHelper;
import v1_8_9.net.minecraft.entity.effect.StatusEffect;
import v1_8_9.net.minecraft.util.Identifier;

public class PotionEffectRenderer implements Listener, IPotionEffectRenderer {

    @Override
    public void run() {
        GlassLoader.getInstance().registerInterface(IPotionEffectRenderer.class, this);
    }

    @Override
    public void render(IPotionEffect.PotionType type, double x, double y, double scale) {
        int id;
        switch (type) {
            case SPEED:
                id = 1;
                break;
            case REGENERATION:
                id = 10;
                break;
            case FIRE_RESISTANCE:
                id = 12;
                break;
            case ABSORPTION:
                id = 22;
                break;
            default:
                id = -1;
                break;
        }

        if (id == -1) {
            System.out.println("unknown effect");
            return;
        }

        int index = StatusEffect.STATUS_EFFECTS[id].method_2444();

        boolean textureEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/container/inventory.png"));
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4d(1, 1, 1, 1);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 0);
        new DrawableHelper().drawTexture(0, 0, index % 8 * 18, 198 + index / 8 * 18, 18, 18);
        GL11.glPopMatrix();

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

}
