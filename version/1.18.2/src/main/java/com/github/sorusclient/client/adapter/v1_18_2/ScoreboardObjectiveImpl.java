/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2;

import com.github.sorusclient.client.adapter.IScoreboardObjective;
import com.github.sorusclient.client.adapter.IScoreboardScore;
import com.github.sorusclient.client.adapter.IText;
import v1_18_2.net.minecraft.scoreboard.ScoreboardObjective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreboardObjectiveImpl implements IScoreboardObjective {

    private final ScoreboardObjective scoreboardObjective;

    public ScoreboardObjectiveImpl(ScoreboardObjective scoreboardObjective) {
        this.scoreboardObjective = scoreboardObjective;
    }

    @Override
    public List<IScoreboardScore> getScores() {
        List<IScoreboardScore> scores = new ArrayList<>();

        for (var score : new ArrayList<>(scoreboardObjective.getScoreboard().getAllPlayerScores(scoreboardObjective))) {
            scores.add(new ScoreboardScoreImpl(score));
        }

        Collections.reverse(scores);
        return scores;
    }

    @Override
    public IText getName() {
        return Util.INSTANCE.textToApiText(scoreboardObjective.getDisplayName());
    }

}
