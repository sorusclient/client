/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2;

import com.github.sorusclient.client.adapter.IScoreboardScore;
import com.github.sorusclient.client.adapter.IText;
import v1_18_2.net.minecraft.scoreboard.ScoreboardPlayerScore;
import v1_18_2.net.minecraft.scoreboard.Team;
import v1_18_2.net.minecraft.text.Text;

public class ScoreboardScoreImpl implements IScoreboardScore {

    private final ScoreboardPlayerScore score;

    public ScoreboardScoreImpl(ScoreboardPlayerScore score) {
        this.score = score;
    }

    @Override
    public IText getName() {
        Team team = score.getScoreboard().getTeam(score.getPlayerName());
        return Util.textToApiText(Team.decorateName(team, Text.of(score.getPlayerName())));
    }

    @Override
    public int getScore() {
        return score.getScore();
    }

}
