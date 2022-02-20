package com.github.sorusclient.client.feature.impl.enhancements

import com.github.sorusclient.client.setting.Category
import com.github.sorusclient.client.setting.SettingConfigure.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager

class Enhancements {

    private var enabled: Setting<Boolean>
    private val fireHeight: Setting<Double>
    private val centeredInventory: Setting<Boolean>

    init {
        SettingManager.settingsCategory
            .apply {
                put("enhancements", HashMap<String, Any>()
                    .apply {
                        put("enabled", Setting(false).also { enabled = it })
                        put("fireHeight", Setting(0.0).also { fireHeight = it })
                        put("centeredInventory", Setting(false).also { centeredInventory = it })
                    })
            }

        SettingManager.mainUICategory
            .apply {
                add(Category("Enhancements"))
                    .apply {
                        add(Toggle(enabled, "Enabled"))
                        add(Slider(fireHeight, "Fire Height", 0.0, 1.0))
                        add(Toggle(centeredInventory, "Centered Inventory"))
                    }
            }
    }

    fun getFireHeightValue(): Double {
        return fireHeight.value
    }

    fun isCenteredInventoryValue(): Boolean {
        return centeredInventory.value
    }

    fun isEnabled(): Boolean {
        return enabled.value
    }

}