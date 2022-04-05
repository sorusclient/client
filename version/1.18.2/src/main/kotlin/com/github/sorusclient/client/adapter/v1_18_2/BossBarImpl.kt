/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2

import com.github.sorusclient.client.adapter.BossBarColor
import com.github.sorusclient.client.adapter.IBossBar
import v1_18_2.net.minecraft.client.gui.hud.ClientBossBar
import v1_18_2.net.minecraft.entity.boss.BossBar

class BossBarImpl(private val bossBar: ClientBossBar) : IBossBar {

    override val name: String
        get() = bossBar.name.string

    override val percentage: Double
        get() = bossBar.percent.toDouble()

    override val color: BossBarColor
        get() = when (bossBar.color!!) {
            BossBar.Color.PINK -> BossBarColor.PINK
            BossBar.Color.BLUE -> BossBarColor.BLUE
            BossBar.Color.RED -> BossBarColor.RED
            BossBar.Color.GREEN -> BossBarColor.GREEN
            BossBar.Color.YELLOW -> BossBarColor.YELLOW
            BossBar.Color.PURPLE -> BossBarColor.PURPLE
            BossBar.Color.WHITE -> BossBarColor.WHITE
        }

}