/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.notification

import com.github.sorusclient.client.util.Rectangle

data class Icon(var icon: String = "", var iconBounds: Rectangle = Rectangle(0.0, 0.0, 1.0, 1.0))