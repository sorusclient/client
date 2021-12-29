package com.github.sorusclient.client.module.impl.environmentchanger.v1_8_9;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.module.ModuleManager;
import com.github.sorusclient.client.module.impl.environmentchanger.EnvironmentChanger;
import v1_8_9.net.minecraft.client.MinecraftClient;

public class EnvironmentChangerHook {

    public static float modifySkyAngle(float angle) {
        EnvironmentChanger environmentChanger = Sorus.getInstance().get(ModuleManager.class).get(EnvironmentChanger.class);
        if (environmentChanger.isEnabled() && environmentChanger.modifyTime()) {
            return MinecraftClient.getInstance().world.dimension.getSkyAngle(environmentChanger.getTime(), 0);
        } else {
            return angle;
        }
    }

    public static float modifyRainGradient(float angle) {
        EnvironmentChanger environmentChanger = Sorus.getInstance().get(ModuleManager.class).get(EnvironmentChanger.class);
        if (environmentChanger.isEnabled() && environmentChanger.modifyWeather()) {
            switch (environmentChanger.getWeather()) {
                case CLEAR:
                    return 0;
                case RAIN:
                    return 1;
            }
        }
        return angle;
    }

}
