package com.github.sorusclient.client.adapter.v1_18_1

import com.github.sorusclient.client.adapter.IScoreboardScore
import com.github.sorusclient.client.adapter.IText
import v1_18_1.net.minecraft.scoreboard.ScoreboardPlayerScore
import v1_18_1.net.minecraft.scoreboard.Team
import v1_18_1.net.minecraft.text.Text

class ScoreboardScoreImpl(private val scoreInternal: ScoreboardPlayerScore) : IScoreboardScore {

    override val name: IText
        get() {
            val playerName = scoreInternal.playerName
            val var14 = scoreInternal.scoreboard.getPlayerTeam(playerName)
            return Util.textToApiText(Team.decorateName(var14, Text.of(playerName)))
        }

    override val score: Int
        get() = scoreInternal.score

}