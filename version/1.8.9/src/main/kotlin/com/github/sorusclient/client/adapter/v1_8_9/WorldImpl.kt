package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IBossBar
import com.github.sorusclient.client.adapter.IScoreboard
import com.github.sorusclient.client.adapter.IWorld
import v1_8_9.net.minecraft.entity.boss.BossBar
import v1_8_9.net.minecraft.world.World

class WorldImpl(private val world: World) : IWorld {
    override val scoreboard: IScoreboard
        get() = com.github.sorusclient.client.adapter.v1_8_9.ScoreboardImpl(world.scoreboard)
    override val bossBars: List<IBossBar>
        get() {
            return if (BossBar.framesToLive > 0) {
                arrayListOf(com.github.sorusclient.client.adapter.v1_8_9.BossBarImpl())
            } else {
                arrayListOf()
            }
        }
}