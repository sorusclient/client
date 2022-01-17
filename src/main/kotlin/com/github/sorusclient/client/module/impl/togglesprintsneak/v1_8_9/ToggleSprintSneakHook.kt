package com.github.sorusclient.client.module.impl.togglesprintsneak.v1_8_9

import com.github.sorusclient.client.module.ModuleManager
import com.github.sorusclient.client.module.impl.togglesprintsneak.ToggleSprintSneak

object ToggleSprintSneakHook {

    @JvmStatic
    fun modifyIsSprintPressed(keyPressed: Boolean): Boolean {
        val toggleSprintSneak = ModuleManager.get<ToggleSprintSneak>()
        return if (toggleSprintSneak.isSprintToggledValue()) {
            true
        } else {
            keyPressed
        }
    }

    @JvmStatic
    fun modifyIsSneakPressed(keyPressed: Boolean): Boolean {
        val toggleSprintSneak = ModuleManager.get<ToggleSprintSneak>()
        return if (toggleSprintSneak.isSneakToggledValue()) {
            true
        } else {
            keyPressed
        }
    }

}