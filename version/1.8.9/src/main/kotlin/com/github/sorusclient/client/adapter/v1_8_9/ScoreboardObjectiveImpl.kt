/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IScoreboardObjective
import com.github.sorusclient.client.adapter.IScoreboardScore
import com.github.sorusclient.client.adapter.IText
import v1_8_9.net.minecraft.scoreboard.ScoreboardObjective
import v1_8_9.net.minecraft.text.LiteralText

class ScoreboardObjectiveImpl(private val scoreboardObjective: ScoreboardObjective) : IScoreboardObjective {
    override val scores: List<IScoreboardScore>
        get() {
            val scores: MutableList<IScoreboardScore> = ArrayList()
            for (score in ArrayList(
                scoreboardObjective.scoreboard.getAllPlayerScores(
                    scoreboardObjective
                )
            )) {
                scores.add(com.github.sorusclient.client.adapter.v1_8_9.ScoreboardScoreImpl(score))
            }
            scores.reverse()
            return scores
        }
    override val name: IText
        get() = com.github.sorusclient.client.adapter.v1_8_9.Util.textToApiText(LiteralText(scoreboardObjective.displayName))
}