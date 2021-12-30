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
            case SLOWNESS:
                id = 2;
                break;
            case HASTE:
                id = 3;
                break;
            case MINING_FATIGUE:
                id = 4;
                break;
            case STRENGTH:
                id = 5;
                break;
            case INSTANT_HEALTH:
                id = 6;
                break;
            case INSTANT_DAMAGE:
                id = 7;
                break;
            case JUMP_BOOST:
                id = 8;
                break;
            case NAUSEA:
                id = 9;
                break;
            case REGENERATION:
                id = 10;
                break;
            case RESISTANCE:
                id = 11;
                break;
            case FIRE_RESISTANCE:
                id = 12;
                break;
            case WATER_BREATHING:
                id = 13;
                break;
            case INVISIBILITY:
                id = 14;
                break;
            case BLINDNESS:
                id = 15;
                break;
            case NIGHT_VISION:
                id = 16;
                break;
            case HUNGER:
                id = 17;
                break;
            case WEAKNESS:
                id = 18;
                break;
            case POISON:
                id = 19;
                break;
            case WITHER:
                id = 20;
                break;
            case HEALTH_BOOST:
                id = 21;
                break;
            case ABSORPTION:
                id = 22;
                break;
            case SATURATION:
                id = 22;
                break;
            default:
                id = -1;
                break;
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
