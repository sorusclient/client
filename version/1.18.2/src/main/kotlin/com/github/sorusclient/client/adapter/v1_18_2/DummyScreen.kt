/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2

import v1_18_2.net.minecraft.client.gui.screen.Screen
import v1_18_2.net.minecraft.text.Text

class DummyScreen : Screen(Text.of("Dummy")) {

    override fun keyPressed(i: Int, i2: Int, i3: Int): Boolean {
        return false
    }

}