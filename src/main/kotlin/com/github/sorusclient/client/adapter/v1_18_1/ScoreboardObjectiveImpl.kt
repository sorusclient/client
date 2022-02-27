package com.github.sorusclient.client.adapter.v1_18_1

import com.github.sorusclient.client.adapter.IScoreboardObjective
import com.github.sorusclient.client.adapter.IScoreboardScore
import com.github.sorusclient.client.adapter.IText
import v1_18_1.net.minecraft.scoreboard.ScoreboardObjective
import v1_18_1.net.minecraft.text.Text

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

    override val name: IText
        get() {
            return Util.textToApiText(scoreboardObjective.displayName)
        }
}