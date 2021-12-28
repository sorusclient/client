package com.github.sorusclient.client.module.impl.itemphysics.v1_8_9;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.module.ModuleManager;
import com.github.sorusclient.client.module.impl.itemphysics.ItemPhysics;
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager;
import v1_8_9.net.minecraft.entity.ItemEntity;

import java.util.HashMap;
import java.util.Map;

public class ItemPhysicsHook {

    public static float modifyItemBob(float bob) {
        ItemPhysics itemPhysics = Sorus.getInstance().get(ModuleManager.class).get(ItemPhysics.class);
        if (itemPhysics.isEnabled()) {
            return 0;
        } else {
            return bob;
        }
    }

    public static float modifyItemRotate(float rotate) {
        ItemPhysics itemPhysics = Sorus.getInstance().get(ModuleManager.class).get(ItemPhysics.class);
        if (itemPhysics.isEnabled()) {
            return 0;
        } else {
            return rotate;
        }
    }

    private static final Map<Object, Long> entityStartTimes = new HashMap<>();

    public static void preRenderItem(ItemEntity entity) {
        ItemPhysics itemPhysics = Sorus.getInstance().get(ModuleManager.class).get(ItemPhysics.class);
        if (itemPhysics.isEnabled()) {
            GlStateManager.translated(0, -0.25, 0);
            GlStateManager.rotatef(90, 1, 0, 0);

            if (!entity.onGround) {
                long startTime = entityStartTimes.computeIfAbsent(entity, k -> System.currentTimeMillis());
                long travelled = System.currentTimeMillis() - startTime;
                GlStateManager.rotatef(travelled * (float) entity.velocityX * -2, 0, 1, 0);
                GlStateManager.rotatef(travelled * (float) entity.velocityZ * 2, 1, 0, 0);
            } else {
                entityStartTimes.remove(entity);
            }
        }
    }

}
