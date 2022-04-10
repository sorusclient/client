/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IBossBar
import com.github.sorusclient.client.adapter.IPlayerEntity
import com.github.sorusclient.client.adapter.IScoreboard
import com.github.sorusclient.client.adapter.IWorld
import v1_8_9.net.minecraft.entity.boss.BossBar
import v1_8_9.net.minecraft.world.World
import java.util.ArrayList

class WorldImpl(private val world: World) : IWorld {

    override val scoreboard: IScoreboard
        get() = ScoreboardImpl(world.scoreboard)

    override val bossBars: List<IBossBar>
        get() {
            return if (BossBar.framesToLive > 0) {
                arrayListOf(BossBarImpl())
            } else {
                arrayListOf()
            }
        }

    override val players: List<IPlayerEntity>
        get() {
            val players = world.playerEntities

            val playersList = ArrayList<IPlayerEntity>()
            for (player in players) {
                playersList.add(PlayerEntityImpl(player))
            }

            return playersList
        }

}