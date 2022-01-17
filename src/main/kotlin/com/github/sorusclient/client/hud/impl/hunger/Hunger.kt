package com.github.sorusclient.client.hud.impl.hunger

import com.github.glassmc.loader.GlassLoader
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.hud.HUDElement

class Hunger : HUDElement("hunger") {

    override val width: Double
        get() = (1 + 8 * 10 + 1 + 1).toDouble()
    override val height: Double
        get() = 11.0

    override fun render(x: Double, y: Double, scale: Double) {
        val player = AdapterManager.getAdapter().player!!
        val hunger = player.hunger.toInt()
        val hungerRenderer = GlassLoader.getInstance().getInterface(IHungerRenderer::class.java)
        for (i in 0..9) {
            val heartX = x + (1 + i * 8) * scale
            hungerRenderer.renderHungerBackground(heartX, y + 1 * scale, scale)
            if ((10 - i) * 2 - 1 < hunger) {
                hungerRenderer.renderHunger(heartX, y + 1 * scale, scale, IHungerRenderer.HeartRenderType.FULL)
            } else if ((10 - i) * 2 - 2 < hunger) {
                hungerRenderer.renderHunger(heartX, y + 1 * scale, scale, IHungerRenderer.HeartRenderType.HALF)
            } else {
                hungerRenderer.renderHunger(heartX, y + 1 * scale, scale, IHungerRenderer.HeartRenderType.EMPTY)
            }
        }
    }

}