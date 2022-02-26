package com.github.sorusclient.client.feature.impl.enhancements

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.PerspectiveMode
import com.github.sorusclient.client.adapter.event.RenderCrosshairEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData

class Enhancements {

    private val fireHeight: Setting<Double>
    private val centeredInventory: Setting<Boolean>
    private val hideCrossHairThirdPerson: Setting<Boolean>

    init {
        SettingManager.settingsCategory
            .apply {
                data["enhancements"] = CategoryData()
                    .apply {
                        data["fireHeight"] = SettingData(Setting(0.0).also { fireHeight = it })
                        data["centeredInventory"] = SettingData(Setting(false).also { centeredInventory = it })
                        data["hideCrossHairThirdPerson"] = SettingData(Setting(false).also { hideCrossHairThirdPerson = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Enhancements"))
                    .apply {
                        add(Slider(fireHeight, "Fire Height", 0.0, 1.0))
                        add(Toggle(centeredInventory, "Centered Inventory"))
                        add(Toggle(hideCrossHairThirdPerson, "Hide Third Person Crosshair"))
                    }
            }

        EventManager.register { event: RenderCrosshairEvent ->
            if (AdapterManager.getAdapter().perspective != PerspectiveMode.FIRST_PERSON && hideCrossHairThirdPerson.value) {
                event.canceled = true
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