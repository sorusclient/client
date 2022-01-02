package com.github.sorusclient.client.module.impl.enhancements.v1_8_9;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.module.ModuleManager;
import com.github.sorusclient.client.module.impl.enhancements.Enhancements;
import org.lwjgl.opengl.GL11;

public class EnhancementsHook {

    public static void preRenderFireFirstPerson() {
        Enhancements enhancements = Sorus.getInstance().get(ModuleManager.class).get(Enhancements.class);
        if (enhancements.isEnabled()) {
            GL11.glPushMatrix();
            GL11.glTranslated(0, -enhancements.getFireHeight() * 0.4, 0);
        }
    }

    public static void postRenderFireFirstPerson() {
        Enhancements enhancements = Sorus.getInstance().get(ModuleManager.class).get(Enhancements.class);
        if (enhancements.isEnabled()) {
            GL11.glPopMatrix();
        }
    }

    public static int modifyPotionOffset(int offset) {
        Enhancements enhancements = Sorus.getInstance().get(ModuleManager.class).get(Enhancements.class);
        if (enhancements.isEnabled() && enhancements.isCenteredInventory()) {
            return 0;
        }
        return offset;
    }

}
