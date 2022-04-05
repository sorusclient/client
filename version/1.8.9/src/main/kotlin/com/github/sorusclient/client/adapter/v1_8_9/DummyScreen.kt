/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9

import v1_8_9.net.minecraft.client.gui.screen.Screen

class DummyScreen : Screen() {
    override fun keyPressed(c: Char, i: Int) {}
}