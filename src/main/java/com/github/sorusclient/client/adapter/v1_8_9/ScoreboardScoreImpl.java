package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IScoreboardScore;
import v1_8_9.net.minecraft.scoreboard.ScoreboardPlayerScore;
import v1_8_9.net.minecraft.scoreboard.Team;

public class ScoreboardScoreImpl implements IScoreboardScore {

    private final ScoreboardPlayerScore score;

    public ScoreboardScoreImpl(ScoreboardPlayerScore score) {
        this.score = score;
    }

    @Override
    public String getName() {
        String playerName = score.getPlayerName();
        Team var14 = this.score.getScoreboard().getPlayerTeam(playerName);
        return Team.method_5565(var14, playerName);
    }

    @Override
    public int getScore() {
        return this.score.getScore();
    }

}
