package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IScoreboardObjective;
import com.github.sorusclient.client.adapter.IScoreboardScore;
import v1_8_9.net.minecraft.scoreboard.ScoreboardObjective;
import v1_8_9.net.minecraft.scoreboard.ScoreboardPlayerScore;

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
        for (ScoreboardPlayerScore score : new ArrayList<>(scoreboardObjective.getScoreboard().getAllPlayerScores(scoreboardObjective))) {
            scores.add(new ScoreboardScoreImpl(score));
        }

        Collections.reverse(scores);

        return scores;
    }

    @Override
    public String getName() {
        return this.scoreboardObjective.getDisplayName();
    }

}
