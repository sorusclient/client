package com.github.sorusclient.client.feature.impl.togglesprintsneak.v1_18_2

import com.github.sorusclient.client.feature.impl.togglesprintsneak.ToggleSprintSneak

@Suppress("UNUSED")
object ToggleSprintSneakHook {

    @JvmStatic
    fun modifyIsSprintPressed(keyPressed: Boolean): Boolean {
        return if (ToggleSprintSneak.isSprintToggledValue()) {
            true
        } else {
            keyPressed
        }
    }

    @JvmStatic
    fun modifyIsSneakPressed(keyPressed: Boolean): Boolean {
        return if (ToggleSprintSneak.isSneakToggledValue()) {
            true
        } else {
            keyPressed
        }
    }

}