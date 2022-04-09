/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IScoreboardScore;
import com.github.sorusclient.client.adapter.IText;
import v1_8_9.net.minecraft.scoreboard.ScoreboardPlayerScore;
import v1_8_9.net.minecraft.scoreboard.Team;
import v1_8_9.net.minecraft.text.LiteralText;

public class ScoreboardScoreImpl implements IScoreboardScore {

    private final ScoreboardPlayerScore score;

    public ScoreboardScoreImpl(ScoreboardPlayerScore score) {
        this.score = score;
    }

    @Override
    public IText getName() {
        Team team = score.getScoreboard().getTeam(score.getPlayerName());
        return Util.textToApiText(new LiteralText(Team.decorateName(team, score.getPlayerName())));
    }

    @Override
    public int getScore() {
        return score.getScore();
    }

}
