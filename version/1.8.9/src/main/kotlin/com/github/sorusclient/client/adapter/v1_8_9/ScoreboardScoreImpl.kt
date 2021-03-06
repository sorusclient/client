/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IScoreboardScore
import com.github.sorusclient.client.adapter.IText
import v1_8_9.net.minecraft.scoreboard.ScoreboardPlayerScore
import v1_8_9.net.minecraft.scoreboard.Team
import v1_8_9.net.minecraft.text.LiteralText

class ScoreboardScoreImpl(private val scoreInternal: ScoreboardPlayerScore) : IScoreboardScore {

    override val name: IText
        get() {
            val playerName = scoreInternal.playerName
            val var14 = scoreInternal.scoreboard.getPlayerTeam(playerName)
            return com.github.sorusclient.client.adapter.v1_8_9.Util.textToApiText(
                LiteralText(
                    Team.decorateName(
                        var14,
                        playerName
                    )
                )
            )
        }

    override val score: Int
        get() = scoreInternal.score

}