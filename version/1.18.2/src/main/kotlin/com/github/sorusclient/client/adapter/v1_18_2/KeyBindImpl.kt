/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2

import com.github.sorusclient.client.adapter.IKeyBind
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.toIdentifier
import v1_18_2.net.minecraft.client.option.KeyBinding
import java.lang.reflect.Field

class KeyBindImpl(private val keyBinding: KeyBinding) : IKeyBind {

    private val boundKey: Field = run {
        val field = KeyBinding::class.java.getDeclaredField("v1_18_2/net/minecraft/client/option/KeyBinding#boundKey".toIdentifier().fieldName)
        field.isAccessible = true

        field
    }

    override val key: Key
        get() = Util.getKey((boundKey.get(keyBinding) as v1_18_2.net.minecraft.client.util.InputUtil.Key).code)

}