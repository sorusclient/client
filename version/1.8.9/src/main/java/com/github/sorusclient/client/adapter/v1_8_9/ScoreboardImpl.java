/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IScoreboard;
import com.github.sorusclient.client.adapter.IScoreboardObjective;
import com.github.sorusclient.client.adapter.ScoreboardSlot;
import v1_8_9.net.minecraft.scoreboard.Scoreboard;

public class ScoreboardImpl implements IScoreboard {

    private final Scoreboard scoreboard;

    public ScoreboardImpl(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public IScoreboardObjective getObjective(ScoreboardSlot slot) {
        var slotName = switch (slot) {
            case SIDEBAR -> "sidebar";
        };

        var scoreboardObjective = scoreboard.getObjectiveForSlot(Scoreboard.getDisplaySlotId(slotName));
        return scoreboardObjective != null ? new ScoreboardObjectiveImpl(scoreboardObjective) : null;
    }

}
