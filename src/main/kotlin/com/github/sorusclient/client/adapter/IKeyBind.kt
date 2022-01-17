package com.github.sorusclient.client.adapter

interface IKeyBind {
    val key: Key

    enum class KeyBindType {
        SPRINT, SNEAK
    }
}