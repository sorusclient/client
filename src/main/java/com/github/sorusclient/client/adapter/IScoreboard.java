package com.github.sorusclient.client.adapter;

public interface IScoreboard {

    IScoreboardObjective getObjective(Slot slot);

    enum Slot {
        SIDEBAR
    }

}
