package com.github.sorusclient.client.adapter

interface IBossBar {
    val name: String?
    val percentage: Double
    val color: BossBarColor
}