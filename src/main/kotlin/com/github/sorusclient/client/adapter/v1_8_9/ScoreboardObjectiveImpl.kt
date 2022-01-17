package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IScoreboardObjective
import com.github.sorusclient.client.adapter.IScoreboardScore
import v1_8_9.net.minecraft.scoreboard.ScoreboardObjective

class ScoreboardObjectiveImpl(private val scoreboardObjective: ScoreboardObjective) : IScoreboardObjective {
    override val scores: List<IScoreboardScore>
        get() {
            val scores: MutableList<IScoreboardScore> = ArrayList()
            for (score in ArrayList(
                scoreboardObjective.scoreboard.getAllPlayerScores(
                    scoreboardObjective
                )
            )) {
                scores.add(ScoreboardScoreImpl(score))
            }
            scores.reverse()
            return scores
        }
    override val name: String
        get() = scoreboardObjective.displayName
}