/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.armor

import com.github.sorusclient.client.adapter.IItem

interface IArmorRenderer {
    fun render(item: IItem, x: Double, y: Double, scale: Double)
    fun renderArmorPlateBackground(x: Double, y: Double, scale: Double)
    fun renderArmorPlate(x: Double, y: Double, scale: Double, armorRenderType: ArmorRenderType?)
    enum class ArmorRenderType {
        FULL, HALF, EMPTY
    }
}