package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IBossBar
import com.github.sorusclient.client.adapter.IScoreboard
import com.github.sorusclient.client.adapter.IWorld
import v1_8_9.net.minecraft.world.World

class WorldImpl(private val world: World) : IWorld {
    override val scoreboard: IScoreboard
        get() = ScoreboardImpl(world.scoreboard)
    override val bossBar: IBossBar
        get() = BossBarImpl()
}