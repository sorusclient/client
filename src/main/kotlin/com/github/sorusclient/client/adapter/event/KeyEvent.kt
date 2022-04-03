package com.github.sorusclient.client.adapter.event

import com.github.sorusclient.client.adapter.Key

class KeyEvent(val key: Key, val isPressed: Boolean, val isRepeat: Boolean)