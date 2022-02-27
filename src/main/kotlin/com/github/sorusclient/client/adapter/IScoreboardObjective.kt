package com.github.sorusclient.client.adapter

interface IScoreboardObjective {
    val scores: List<IScoreboardScore>
    val name: IText
}