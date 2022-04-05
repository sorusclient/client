/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.hunger

interface IHungerRenderer {
    fun renderHunger(x: Double, y: Double, scale: Double, heartRenderType: HeartRenderType)
    fun renderHungerBackground(x: Double, y: Double, scale: Double)
    enum class HeartRenderType {
        FULL, HALF, EMPTY
    }
}