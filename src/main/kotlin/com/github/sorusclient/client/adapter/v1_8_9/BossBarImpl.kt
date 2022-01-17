package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IBossBar
import v1_8_9.net.minecraft.entity.boss.BossBar

class BossBarImpl : IBossBar {
    override val name: String?
        get() = BossBar.name
    override val percentage: Double
        get() = BossBar.percent.toDouble()
    override val isBossBar: Boolean
        get() = BossBar.framesToLive > 0
}