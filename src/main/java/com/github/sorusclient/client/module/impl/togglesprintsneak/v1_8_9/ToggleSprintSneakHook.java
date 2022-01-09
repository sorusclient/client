package com.github.sorusclient.client.module.impl.togglesprintsneak.v1_8_9;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.module.ModuleManager;
import com.github.sorusclient.client.module.impl.togglesprintsneak.ToggleSprintSneak;

public class ToggleSprintSneakHook {

    public static boolean modifyIsSprintPressed(boolean keyPressed) {
        ToggleSprintSneak toggleSprintSneak = Sorus.getInstance().get(ModuleManager.class).get(ToggleSprintSneak.class);
        if (toggleSprintSneak.isSprintToggled()) {
            return true;
        } else {
            return keyPressed;
        }
    }

    public static boolean modifyIsSneakPressed(boolean keyPressed) {
        ToggleSprintSneak toggleSprintSneak = Sorus.getInstance().get(ModuleManager.class).get(ToggleSprintSneak.class);
        if (toggleSprintSneak.isSneakToggled()) {
            return true;
        } else {
            return keyPressed;
        }
    }

}
