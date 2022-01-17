package com.github.sorusclient.client.hud.impl.hunger

interface IHungerRenderer {
    fun renderHunger(x: Double, y: Double, scale: Double, heartRenderType: HeartRenderType)
    fun renderHungerBackground(x: Double, y: Double, scale: Double)
    enum class HeartRenderType {
        FULL, HALF, EMPTY
    }
}