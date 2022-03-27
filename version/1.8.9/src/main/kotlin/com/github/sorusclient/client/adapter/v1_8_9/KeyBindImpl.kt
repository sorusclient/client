package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IKeyBind
import com.github.sorusclient.client.adapter.Key
import v1_8_9.net.minecraft.client.options.KeyBinding

class KeyBindImpl(private val keyBinding: KeyBinding) : IKeyBind {
    override val key: Key
        get() = com.github.sorusclient.client.adapter.v1_8_9.Util.getKey(keyBinding.code)
}