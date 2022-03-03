package com.github.sorusclient.client.adapter.v1_18_1

import com.github.glassmc.loader.util.Identifier
import com.github.sorusclient.client.adapter.IKeyBind
import com.github.sorusclient.client.adapter.Key
import v1_18_1.net.minecraft.client.option.KeyBinding
import java.lang.reflect.Field

class KeyBindImpl(private val keyBinding: KeyBinding) : IKeyBind {

    private val boundKey: Field = run {
        val field = KeyBinding::class.java.getDeclaredField(Identifier.parse("v1_18_1/net/minecraft/client/option/KeyBinding#boundKey").fieldName)
        field.isAccessible = true

        field
    }

    override val key: Key
        get() = Util.getKey((boundKey.get(keyBinding) as v1_18_1.net.minecraft.client.util.InputUtil.Key).code)

}