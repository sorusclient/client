package com.github.sorusclient.client.module.impl.perspective.v1_8_9;

import com.github.glassmc.loader.GlassLoader;
import com.github.glassmc.loader.Listener;
import com.github.sorusclient.client.module.impl.perspective.IPerspectiveHelper;
import v1_8_9.net.minecraft.client.MinecraftClient;

public class PerspectiveHelper implements Listener, IPerspectiveHelper {

    @Override
    public void run() {
        GlassLoader.getInstance().registerInterface(IPerspectiveHelper.class, new PerspectiveHelper());
    }

    @Override
    public void onToggle() {
        PerspectiveHook.yaw = MinecraftClient.getInstance().player.yaw;
        PerspectiveHook.prevYaw = MinecraftClient.getInstance().player.prevYaw;
        PerspectiveHook.pitch = MinecraftClient.getInstance().player.pitch;
        PerspectiveHook.prevPitch = MinecraftClient.getInstance().player.prevPitch;

        if (MinecraftClient.getInstance().options.perspective == 2) {
            PerspectiveHook.yaw += 180;
            PerspectiveHook.prevYaw += 180;

            PerspectiveHook.pitch = -PerspectiveHook.pitch;
            PerspectiveHook.prevPitch = -PerspectiveHook.prevPitch;
        }
    }

}
