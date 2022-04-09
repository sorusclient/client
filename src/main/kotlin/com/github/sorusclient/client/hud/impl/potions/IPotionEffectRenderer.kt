/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.potions

import com.github.sorusclient.client.adapter.PotionType

interface IPotionEffectRenderer {
    fun render(type: PotionType?, x: Double, y: Double, scale: Double)
}