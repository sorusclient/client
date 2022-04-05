/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.hotbar

import com.github.sorusclient.client.adapter.IItem

interface IHotBarRenderer {
    fun renderBackground(x: Double, y: Double, scale: Double)
    fun renderItem(x: Double, y: Double, scale: Double, item: IItem)
    fun renderSelectedSlot(x: Double, y: Double, scale: Double)
}