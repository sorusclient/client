/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.bossbar

import com.github.sorusclient.client.adapter.BossBarColor

interface IBossBarRenderer {
    fun renderBossBar(x: Double, y: Double, scale: Double, percent: Double, color: BossBarColor)
}