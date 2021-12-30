package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IScoreboard;
import com.github.sorusclient.client.adapter.IScoreboardObjective;
import v1_8_9.net.minecraft.scoreboard.Scoreboard;
import v1_8_9.net.minecraft.scoreboard.ScoreboardObjective;

import java.awt.*;

public class ScoreboardImpl implements IScoreboard {

    private final Scoreboard scoreboard;

    public ScoreboardImpl(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public IScoreboardObjective getObjective(Slot slot) {
        String slotName;
        switch (slot) {
            case SIDEBAR:
                slotName = "sidebar";
                break;
            default:
                slotName = "unknown";
                break;
        }

        ScoreboardObjective scoreboardObjective = this.scoreboard.getObjectiveForSlot(Scoreboard.getDisplaySlotId(slotName));
        return scoreboardObjective != null ? new ScoreboardObjectiveImpl(scoreboardObjective) : null;
    }

}
