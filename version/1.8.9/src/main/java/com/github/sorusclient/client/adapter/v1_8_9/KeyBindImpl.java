/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IKeyBind;
import com.github.sorusclient.client.adapter.Key;
import v1_8_9.net.minecraft.client.options.KeyBinding;

public class KeyBindImpl implements IKeyBind {

    protected final KeyBinding keyBinding;

    public KeyBindImpl(KeyBinding keyBinding) {
        this.keyBinding = keyBinding;
    }

    @Override
    public Key getKey() {
        return Util.INSTANCE.getKey(keyBinding.getCode());
    }

}
