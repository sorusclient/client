package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IKeyBind;
import com.github.sorusclient.client.adapter.Key;
import v1_8_9.net.minecraft.client.options.KeyBinding;

public class KeyBindImpl implements IKeyBind {

    private final KeyBinding keyBinding;

    public KeyBindImpl(KeyBinding keyBinding) {
        this.keyBinding = keyBinding;
    }

    @Override
    public Key getKey() {
        return Util.getKey(this.keyBinding.getCode());
    }

}
