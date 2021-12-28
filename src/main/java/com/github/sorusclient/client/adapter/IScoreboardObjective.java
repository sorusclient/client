package com.github.sorusclient.client.adapter;

import java.util.List;

public interface IScoreboardObjective {

    List<IScoreboardScore> getScores();
    String getName();

}
