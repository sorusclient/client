package com.github.sorusclient.client.adapter

interface IScoreboard {
    fun getObjective(slot: Slot?): IScoreboardObjective?
    enum class Slot {
        SIDEBAR
    }
}