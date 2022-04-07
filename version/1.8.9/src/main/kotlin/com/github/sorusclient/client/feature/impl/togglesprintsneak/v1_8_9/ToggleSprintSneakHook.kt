/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.togglesprintsneak.v1_8_9

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