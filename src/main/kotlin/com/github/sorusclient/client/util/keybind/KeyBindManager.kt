package com.github.sorusclient.client.util.keybind

import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.event.EventManager

object KeyBindManager {

    private val keyBinds: MutableList<KeyBind> = ArrayList()

    init {
        EventManager.register(this::onKey)
    }

    fun register(keyBind: KeyBind) {
        keyBinds.add(keyBind)
    }

    private val currentKeys: MutableList<Key> = ArrayList()

    private fun onKey(event: KeyEvent) {
        if (event.isPressed && !event.isRepeat) {
            currentKeys += event.key

            for (keyBind in keyBinds) {
                if (currentKeys.containsAll(keyBind.keyCheck())) {
                    keyBind.action(true)
                }
            }
        } else if (!event.isPressed) {
            currentKeys.clear()

            for (keyBind in keyBinds) {
                if (arrayListOf(event.key).containsAll(keyBind.keyCheck())) {
                    keyBind.action(false)
                }
            }
        }
    }

}