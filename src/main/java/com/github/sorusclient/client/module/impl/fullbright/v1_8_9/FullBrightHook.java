package com.github.sorusclient.client.module.impl.fullbright.v1_8_9;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.module.ModuleManager;
import com.github.sorusclient.client.module.impl.fullbright.FullBright;

public class FullBrightHook {

    public static float modifyGamma(float gamma) {
        FullBright fullBright = Sorus.getInstance().get(ModuleManager.class).get(FullBright.class);
        if (fullBright.isEnabled()) {
            return 50f;
        } else {
            return gamma;
        }
    }

}
