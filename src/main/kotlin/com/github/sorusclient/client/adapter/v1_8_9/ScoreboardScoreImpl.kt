package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IScoreboardScore
import v1_8_9.net.minecraft.scoreboard.ScoreboardPlayerScore
import v1_8_9.net.minecraft.scoreboard.Team

class ScoreboardScoreImpl(private val scoreInternal: ScoreboardPlayerScore) : IScoreboardScore {

    override val name: String
        get() {
            val playerName = scoreInternal.playerName
            val var14 = scoreInternal.scoreboard.getPlayerTeam(playerName)
            return Team.method_5565(var14, playerName)
        }

    override val score: Int
        get() = scoreInternal.score

}