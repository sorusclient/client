package com.github.sorusclient.client.adapter.v1_18_1

import com.github.sorusclient.client.adapter.IBossBar
import v1_18_1.net.minecraft.client.gui.hud.ClientBossBar

class BossBarImpl(private val bossBar: ClientBossBar) : IBossBar {

    override val name: String
        get() = bossBar.name.string

    override val percentage: Double
        get() = bossBar.percent.toDouble()

}