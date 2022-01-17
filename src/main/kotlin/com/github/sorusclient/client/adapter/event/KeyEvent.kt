package com.github.sorusclient.client.adapter.event

import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.event.Event

class KeyEvent(val key: Key, val character: Char, val isPressed: Boolean, val isRepeat: Boolean) : Event()