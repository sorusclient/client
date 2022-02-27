package com.github.sorusclient.client.hud.impl.hotbar

import com.github.glassmc.loader.api.GlassLoader
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.IPlayerInventory
import com.github.sorusclient.client.hud.HUDElement
import com.github.sorusclient.client.util.Color

class HotBar : HUDElement("hotBar") {

    override val width: Double
        get() = 184.0
    override val height: Double
        get() = 24.0

    override val displayName: String
        get() = "HotBar"

    override fun render(x: Double, y: Double, scale: Double) {
        val inventory = AdapterManager.getAdapter().player!!.inventory
        val hotBarRenderer = GlassLoader.getInstance().getInterface(IHotBarRenderer::class.java)
        hotBarRenderer.renderBackground(x + 1 * scale, y + 1 * scale, scale)
        hotBarRenderer.renderSelectedSlot(x + 20 * inventory.selectedSlot.ordinal * scale, y, scale)
        for (i in 0..8) {
            val slot = IPlayerInventory.Slot.values()[i]
            val item = inventory.getItem(slot)
            if (item != null) {
                hotBarRenderer.renderItem(x + 4 * scale + i * 20 * scale, y + 4 * scale, scale, item)
            }
        }
    }

}