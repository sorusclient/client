/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.potions

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.IPotionEffect
import com.github.sorusclient.client.adapter.PotionType
import com.github.sorusclient.client.hud.HUDElement
import com.github.sorusclient.client.hud.HUDManager
import com.github.sorusclient.client.util.Color

class Potions : HUDElement("potionStatus") {

    override val width: Double
        get() {
            val renderer = AdapterManager.getAdapter().renderer
            val fontRenderer = renderer.getFontRenderer("minecraft")!!
            var maxWidth = 0.0
            for (effect in effects) {
                val width = fontRenderer.getWidth(effect.name + " " + getAmplifierString(effect.amplifier))
                maxWidth = maxWidth.coerceAtLeast(width)
            }
            return 24 + maxWidth + 3
        }
    override val height: Double
        get() {
            val renderer = AdapterManager.getAdapter().renderer
            val fontRenderer = renderer.getFontRenderer("minecraft")!!
            val effects = effects
            return if (effects.isEmpty()) 0.0 else 3 + effects.size * (5 + fontRenderer.getHeight() * 2)
        }

    override val displayName: String
        get() = "Potions"

    override fun render(x: Double, y: Double, scale: Double) {
        val renderer = AdapterManager.getAdapter().renderer
        val fontRenderer = renderer.getFontRenderer("minecraft")!!
        val potionEffectRenderer = InterfaceManager.get(IPotionEffectRenderer::class.java)
        renderer.drawRectangle(x, y, width * scale, height * scale, Color.fromRGB(0, 0, 0, 100))
        var textY = y + 3 * scale
        for (effect in effects) {
            fontRenderer.drawString(
                effect.name + " " + getAmplifierString(effect.amplifier),
                x + 24 * scale,
                textY,
                scale,
                Color.WHITE
            )
            fontRenderer.drawString(
                effect.duration,
                x + 24 * scale,
                textY + 2 * scale + fontRenderer.getHeight() * scale,
                scale,
                Color.WHITE
            )
            potionEffectRenderer.render(effect.type, x + 3 * scale, textY, scale)
            textY += (5 + fontRenderer.getHeight() * 2) * scale
        }
    }

    private fun getAmplifierString(amplifier: Int): String {
        return when (amplifier) {
            2 -> "II"
            3 -> "III"
            4 -> "IV"
            5 -> "V"
            else -> ""
        }
    }

    private val effects: List<IPotionEffect>
        get() {
            val adapter = AdapterManager.getAdapter()
            val editing = HUDManager.isHudEditScreenOpen.get()
            val realEffects = adapter.player!!.effects
            return if (!editing || realEffects.isNotEmpty()) {
                realEffects
            } else {
                val fakeEffects: MutableList<IPotionEffect> = ArrayList()
                fakeEffects.add(FakePotionEffect("1:29", "Fire Resistance", 1, PotionType.FIRE_RESISTANCE))
                fakeEffects
            }
        }

    class FakePotionEffect(private val duration: String, private val name: String, private val amplifier: Int, val potionType: PotionType): IPotionEffect {

        override fun getDuration(): String {
            return duration
        }

        override fun getName(): String {
            return name
        }

        override fun getAmplifier(): Int {
            return amplifier
        }

        override fun getType(): PotionType {
            return potionType
        }

    }

}