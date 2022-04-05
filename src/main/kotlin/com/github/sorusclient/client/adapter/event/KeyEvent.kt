/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.event

import com.github.sorusclient.client.adapter.Key

class KeyEvent(val key: Key, val isPressed: Boolean, val isRepeat: Boolean)