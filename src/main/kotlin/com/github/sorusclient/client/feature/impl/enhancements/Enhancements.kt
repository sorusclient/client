package com.github.sorusclient.client.feature.impl.enhancements

import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData

class Enhancements {

    private val fireHeight: Setting<Double>
    private val centeredInventory: Setting<Boolean>

    init {
        SettingManager.settingsCategory
            .apply {
                data["enhancements"] = CategoryData()
                    .apply {
                        data["fireHeight"] = SettingData(Setting(0.0).also { fireHeight = it })
                        data["centeredInventory"] = SettingData(Setting(false).also { centeredInventory = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Enhancements"))
                    .apply {
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

}