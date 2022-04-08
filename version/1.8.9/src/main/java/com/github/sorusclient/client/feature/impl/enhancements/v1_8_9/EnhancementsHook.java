/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.enhancements.v1_8_9;

import com.github.sorusclient.client.feature.impl.enhancements.Enhancements;
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.org.lwjgl.opengl.Display;

@SuppressWarnings("unused")
public class EnhancementsHook {

    public static Object options;

    public static void preRenderFireFirstPerson() {
        GlStateManager.pushMatrix();
        GlStateManager.translated(0.0D, -Enhancements.INSTANCE.getFireHeightValue() * 0.4D, 0.0D);
    }

    public static void postRenderFireFirstPerson() {
        GlStateManager.popMatrix();
    }

    public static int modifyPotionOffset(int offset) {
        return com.github.sorusclient.client.feature.impl.enhancements.v1_8_9.Enhancements.INSTANCE.isCenteredInventoryValue() ? 0 : offset;
    }

    public static float modifyPitch(float pitch) {
        return MinecraftClient.getInstance().options.perspective == 2 ? -pitch : pitch;
    }

    public static float modifyPrevPitch(float prevPitch) {
        return MinecraftClient.getInstance().options.perspective == 2 ? -prevPitch : prevPitch;
    }

    public static void onSave() {
        Enhancements.INSTANCE.saveSettings();
    }

    public static void onLoad(Object options) {
        EnhancementsHook.options = options;
        Enhancements.INSTANCE.loadSettings();
    }

    public static void onStop() {
        Display.destroy();
    }

    private EnhancementsHook() {
    }
}
