package com.github.sorusclient.client.module.impl.enhancements

import com.github.sorusclient.client.module.ModuleDisableable
import com.github.sorusclient.client.setting.ConfigurableData
import com.github.sorusclient.client.setting.ConfigurableData.Slider
import com.github.sorusclient.client.setting.ConfigurableData.Toggle
import com.github.sorusclient.client.setting.Setting

class Enhancements : ModuleDisableable("enhancements") {

    private val fireHeight: Setting<Double>
    private val centeredInventory: Setting<Boolean>

    init {
        register("fireHeight", Setting(0.0).also { fireHeight = it })
        register("centeredInventory", Setting(false).also { centeredInventory = it })
    }

    fun getFireHeightValue(): Double {
        return fireHeight.value
    }

    fun isCenteredInventoryValue(): Boolean {
        return centeredInventory.value
    }

    override fun addSettings(settings: MutableList<ConfigurableData>) {
        super.addSettings(settings)
        settings.add(Slider("Fire Height", fireHeight, 0.0, 1.0))
        settings.add(Toggle("Centered Inventory", centeredInventory))
    }
}