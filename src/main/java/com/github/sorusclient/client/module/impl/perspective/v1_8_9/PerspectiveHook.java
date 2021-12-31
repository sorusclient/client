package com.github.sorusclient.client.module.impl.perspective.v1_8_9;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.module.ModuleManager;
import com.github.sorusclient.client.module.impl.perspective.Perspective;
import v1_8_9.net.minecraft.util.math.MathHelper;

public class PerspectiveHook {

    private static boolean x = true;

    public static float yaw = 0;
    public static float prevYaw = 0;
    public static float pitch = 0;
    public static float prevPitch = 0;

    public static float modifyDelta(float delta) {
        Perspective perspective = Sorus.getInstance().get(ModuleManager.class).get(Perspective.class);

        boolean isYaw = x;
        x = !x;

        if (perspective.isEnabled() && perspective.isToggled()) {
            if (isYaw) {
                float oldYaw = yaw;
                yaw = (float)((double)yaw + (double) delta * 0.15D);
                prevYaw += yaw - oldYaw;
            } else {
                float oldPitch = pitch;
                pitch = (float)((double)pitch - (double) delta * 0.15D);
                pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
                prevPitch += pitch - oldPitch;
            }

            return 0;
        } else {
            return delta;
        }
    }

    public static float modifyPitch(float pitch) {
        Perspective perspective = Sorus.getInstance().get(ModuleManager.class).get(Perspective.class);

        if (perspective.isEnabled() && perspective.isToggled()) {
            return PerspectiveHook.pitch;
        }
        return pitch;
    }

    public static float modifyYaw(float yaw) {
        Perspective perspective = Sorus.getInstance().get(ModuleManager.class).get(Perspective.class);

        if (perspective.isEnabled() && perspective.isToggled()) {
            return PerspectiveHook.yaw;
        }
        return yaw;
    }

    public static float modifyPrevPitch(float prevPitch) {
        Perspective perspective = Sorus.getInstance().get(ModuleManager.class).get(Perspective.class);

        if (perspective.isEnabled() && perspective.isToggled()) {
            return PerspectiveHook.prevPitch;
        }
        return prevPitch;
    }

    public static float modifyPrevYaw(float prevYaw) {
        Perspective perspective = Sorus.getInstance().get(ModuleManager.class).get(Perspective.class);

        if (perspective.isEnabled() && perspective.isToggled()) {
            return PerspectiveHook.prevYaw;
        }
        return prevYaw;
    }

}
