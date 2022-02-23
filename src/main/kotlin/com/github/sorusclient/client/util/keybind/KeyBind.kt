package com.github.sorusclient.client.util.keybind

import com.github.sorusclient.client.adapter.Key

class KeyBind(val keyCheck: () -> List<Key>, val action: (Boolean) -> Unit)