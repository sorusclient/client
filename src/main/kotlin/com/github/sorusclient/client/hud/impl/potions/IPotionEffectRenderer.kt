package com.github.sorusclient.client.hud.impl.potions

import com.github.sorusclient.client.adapter.IPotionEffect.PotionType

interface IPotionEffectRenderer {
    fun render(type: PotionType?, x: Double, y: Double, scale: Double)
}