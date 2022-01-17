package com.github.sorusclient.client.hud.impl.bossbar

interface IBossBarRenderer {
    fun renderBossBar(x: Double, y: Double, scale: Double, percent: Double)
}