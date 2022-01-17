package com.github.sorusclient.client.hud.impl.armor

import com.github.glassmc.loader.GlassLoader
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.adapter.IItem.ItemType
import com.github.sorusclient.client.hud.HUDElement
import com.github.sorusclient.client.setting.ConfigurableData
import com.github.sorusclient.client.setting.ConfigurableData.ClickThrough
import com.github.sorusclient.client.setting.ConfigurableData.Toggle
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.ui.UserInterface
import com.github.sorusclient.client.util.Color
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

class Armor : HUDElement("armor") {

    //TODO: Add option for reversing image & durability
    private var mode: Setting<Mode>
    private var showHelmet: Setting<Boolean>
    private var showChestplate: Setting<Boolean>
    private var showLeggings: Setting<Boolean>
    private var showBoots: Setting<Boolean>

    init {
        register("mode", Setting(Mode.TOTAL).also {
            mode = it
        })
        register("showHelmet", Setting(true).also {
            showHelmet = it
        })
        register("showChestplate", Setting(true).also {
            showChestplate = it
        })
        register("showLeggings", Setting(true).also {
            showLeggings = it
        })
        register("showBoots", Setting(true).also {
            showBoots = it
        })
    }

    override fun render(x: Double, y: Double, scale: Double) {
        val player = AdapterManager.getAdapter().player!!
        val renderer = AdapterManager.getAdapter().renderer
        val fontRenderer = renderer.getFontRenderer("minecraft")!!
        val armorRenderer = GlassLoader.getInstance().getInterface(IArmorRenderer::class.java)
        when (mode.value) {
            Mode.INDIVIDUAL -> {
                renderer.drawRectangle(x, y, width * scale, height * scale, Color.fromRGB(0, 0, 0, 100))
                var textY = y + 3 * scale
                for ((index, armor) in armor.withIndex()) {
                    if (shouldDisplay(armor, index)) {
                        armorRenderer.render(armor, x + 3 * scale, textY, scale)
                        fontRenderer.drawString(
                            String.format("%.0f", armor.remainingDurability),
                            x + 24 * scale,
                            textY + 5 * scale,
                            scale,
                            Color.WHITE
                        )
                        textY += (5 + fontRenderer.height * 2) * scale
                    }
                }
            }
            Mode.TOTAL -> {
                val armor = player.armorProtection.toInt()
                var i = 0
                while (i < 10) {
                    val heartX = x + (1 + i * 8) * scale
                    armorRenderer.renderArmorPlateBackground(heartX, y + 1 * scale, scale)
                    if (i * 2 + 1 < armor) {
                        armorRenderer.renderArmorPlate(
                            heartX,
                            y + 1 * scale,
                            scale,
                            IArmorRenderer.ArmorRenderType.FULL
                        )
                    } else if (i * 2 < armor) {
                        armorRenderer.renderArmorPlate(
                            heartX,
                            y + 1 * scale,
                            scale,
                            IArmorRenderer.ArmorRenderType.HALF
                        )
                    } else {
                        armorRenderer.renderArmorPlate(
                            heartX,
                            y + 1 * scale,
                            scale,
                            IArmorRenderer.ArmorRenderType.EMPTY
                        )
                    }
                    i++
                }
            }
        }
    }

    private fun shouldDisplay(iItem: IItem?, index: Int): Boolean {
        if (iItem == null) return false
        when (index) {
            0 -> return showHelmet.value
            1 -> return showChestplate.value
            2 -> return showLeggings.value
            3 -> return showBoots.value
        }
        return false
    }

    private val armor: List<IItem>
        get() {
            val adapter = AdapterManager.getAdapter()
            val editing = UserInterface.isHudEditScreenOpen()
            val realArmor = adapter.player!!.armor
            return if (!editing || realArmor.stream().anyMatch { o: IItem? ->
                    Objects.nonNull(
                        o
                    )
                }) {
                realArmor.filterNotNull()
            } else {
                val fakeArmor: MutableList<IItem> = ArrayList()
                fakeArmor.add(FakeArmorItem(188.0, 240.0, ItemType.IRON_HELMET))
                fakeArmor.add(FakeArmorItem(312.0, 350.0, ItemType.DIAMOND_CHESTPLATE))
                fakeArmor.add(FakeArmorItem(98.0, 146.0, ItemType.GOLD_LEGGINGS))
                fakeArmor.add(FakeArmorItem(68.0, 72.0, ItemType.LEATHER_BOOTS))
                fakeArmor
            }
        }
    override val width: Double
        get() = when (mode.value) {
            Mode.INDIVIDUAL -> 60.0
            Mode.TOTAL -> (1 + 8 * 10 + 1 + 1).toDouble()
        }
    override val height: Double
        get() {
            return when (mode.value) {
                Mode.INDIVIDUAL -> {
                    val renderer = AdapterManager.getAdapter().renderer
                    val fontRenderer = renderer.getFontRenderer("minecraft")!!
                    val index = AtomicInteger(0)
                    val armor = armor.stream().filter { item: IItem? ->
                        val display: Boolean = shouldDisplay(item, index.get())
                        index.set(index.get() + 1)
                        display
                    }.collect(Collectors.toList())
                    if (armor.size == 0) 0.0 else 3 + armor.size * (5 + fontRenderer.height * 2)
                }
                Mode.TOTAL -> 11.0
            }
        }

    override fun addSettings(settings: MutableList<ConfigurableData>) {
        super.addSettings(settings)
        settings.add(ClickThrough("Mode", mode))
        settings.add(Toggle("Show Helmet", showHelmet))
        settings.add(Toggle("Show Chestplate", showChestplate))
        settings.add(Toggle("Show Leggings", showLeggings))
        settings.add(Toggle("Show Boots", showBoots))
    }

    private class FakeArmorItem(
        override val remainingDurability: Double,
        override val maxDurability: Double,
        override val type: ItemType
    ) : IItem {

        override val inner: Any?
            get() = null

        override val count: Int
            get() = 0

    }

    enum class Mode {
        INDIVIDUAL, TOTAL
    }

    private enum class ArmorType {
        HELMET, CHESTPLATE, LEGGINGS, BOOTS
    }

}