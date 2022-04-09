/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2;

import v1_18_2.net.minecraft.client.gui.screen.Screen;
import v1_18_2.net.minecraft.text.Text;

public class DummyScreen extends Screen {

    protected DummyScreen() {
        super(Text.of("Dummy"));
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        return false;
    }

}
