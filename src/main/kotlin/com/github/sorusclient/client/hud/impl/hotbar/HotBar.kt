package com.github.sorusclient.client.hud.impl.hotbar

import com.github.glassmc.loader.api.GlassLoader
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.IPlayerInventory
import com.github.sorusclient.client.hud.HUDElement
import com.github.sorusclient.client.util.Color
import org.lwjgl.opengl.GL11

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
        val minecraftFontRenderer = AdapterManager.getAdapter().renderer.getFontRenderer("minecraft")!!
        for (i in 0..8) {
            val slot = IPlayerInventory.Slot.values()[i]
            val item = inventory.getItem(slot)
            if (item != null) {
                hotBarRenderer.renderItem(x + 4 * scale + i * 20 * scale, y + 4 * scale, scale, item)
                if (item.count > 1) {
                    val itemCount = item.count.toString()
                    minecraftFontRenderer.drawStringShadowed(
                        itemCount,
                        x + 20 * scale + i * 20 * scale - minecraftFontRenderer.getWidth(itemCount) * scale,
                        y + 21 * scale - minecraftFontRenderer.getHeight() * scale,
                        scale,
                        Color.WHITE,
                        Color.fromRGB(70, 70, 70, 255)
                    )
                }
            }
        }
    }

}