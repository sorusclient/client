/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IScoreboard
import com.github.sorusclient.client.adapter.IScoreboardObjective
import v1_8_9.net.minecraft.scoreboard.Scoreboard

class ScoreboardImpl(private val scoreboard: Scoreboard) : IScoreboard {

    override fun getObjective(slot: IScoreboard.Slot): IScoreboardObjective? {
        val slotName: String = when (slot) {
            IScoreboard.Slot.SIDEBAR -> "sidebar"
            else -> "unknown"
        }
        val scoreboardObjective = scoreboard.getObjectiveForSlot(Scoreboard.getDisplaySlotId(slotName))
        return scoreboardObjective?.let { ScoreboardObjectiveImpl(it) }
    }

}