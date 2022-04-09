/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2;

import com.github.sorusclient.client.IdentifierKt;
import com.github.sorusclient.client.adapter.IKeyBind;
import com.github.sorusclient.client.adapter.Key;
import v1_18_2.net.minecraft.client.option.KeyBinding;
import v1_18_2.net.minecraft.client.util.InputUtil;

import java.lang.reflect.Field;

public class KeyBindImpl implements IKeyBind {

    private final KeyBinding keyBinding;
    private final Field boundKey;

    public KeyBindImpl(KeyBinding keyBinding) {
        this.keyBinding = keyBinding;

        try {
            Field field = KeyBinding.class.getDeclaredField(IdentifierKt.toIdentifier("v1_18_2/net/minecraft/client/option/KeyBinding#boundKey").getFieldName());
            field.setAccessible(true);
            boundKey = field;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }

    }

    @Override
    public Key getKey() {
        try {
            return Util.getKey(((InputUtil.Key) boundKey.get(keyBinding)).getCode());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

}
