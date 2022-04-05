/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.health

interface IHealthRenderer {
    fun renderHeart(x: Double, y: Double, scale: Double, heartType: HeartType, heartRenderType: HeartRenderType)
    fun renderHeartBackground(x: Double, y: Double, scale: Double, backgroundType: BackgroundType?)
    enum class HeartType {
        HEALTH, ABSORPTION
    }

    enum class HeartRenderType {
        FULL, HALF_EMPTY, HALF_DAMAGE, DAMAGE, DAMAGE_EMPTY, EMPTY
    }

    enum class BackgroundType {
        FLASHING_OUTLINE, STANDARD
    }
}