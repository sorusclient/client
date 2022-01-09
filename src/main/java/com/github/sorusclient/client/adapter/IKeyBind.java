package com.github.sorusclient.client.adapter;

public interface IKeyBind {

    Key getKey();

    enum KeyBindType {
        SPRINT,
        SNEAK
    }

}
