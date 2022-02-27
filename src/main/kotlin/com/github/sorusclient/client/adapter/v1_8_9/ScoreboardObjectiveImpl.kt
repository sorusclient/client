package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IScoreboardObjective
import com.github.sorusclient.client.adapter.IScoreboardScore
import com.github.sorusclient.client.adapter.IText
import v1_8_9.net.minecraft.scoreboard.ScoreboardObjective
import v1_8_9.net.minecraft.text.BaseText
import v1_8_9.net.minecraft.text.LiteralText
import v1_8_9.net.minecraft.text.Text

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
        get() = Util.textToApiText(LiteralText(scoreboardObjective.displayName))
}