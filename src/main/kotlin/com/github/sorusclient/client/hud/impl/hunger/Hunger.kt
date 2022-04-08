/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.hunger

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.GameMode
import com.github.sorusclient.client.hud.HUDElement
import com.github.sorusclient.client.hud.HUDManager

class Hunger : HUDElement("hunger") {

    override val width: Double
        get() = (1 + 8 * 10 + 1 + 1).toDouble()
    override val height: Double
        get() = 11.0

    override val displayName: String
        get() = "Hunger"

    override fun render(x: Double, y: Double, scale: Double) {
        if (!(AdapterManager.getAdapter().gameMode == GameMode.SURVIVAL || AdapterManager.getAdapter().gameMode == GameMode.ADVENTURE) && !HUDManager.isHudEditScreenOpen.get()) return

        val player = AdapterManager.getAdapter().player!!
        val hunger = player.hunger.toInt()
        val hungerRenderer = InterfaceManager.get(IHungerRenderer::class.java)
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