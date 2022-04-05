/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.util.keybind

import com.github.sorusclient.client.adapter.Key

class KeyBind(val keyCheck: () -> List<Key>, val action: (Boolean) -> Unit)