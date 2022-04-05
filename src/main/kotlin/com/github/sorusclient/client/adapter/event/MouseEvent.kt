/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.event

import com.github.sorusclient.client.adapter.Button

class MouseEvent(val button: Button?, val isPressed: Boolean, val x: Double, val y: Double, val wheel: Double)