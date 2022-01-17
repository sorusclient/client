package com.github.sorusclient.client.hud.impl.experience

interface IExperienceRenderer {
    fun renderExperienceBar(x: Double, y: Double, scale: Double, percent: Double)
}