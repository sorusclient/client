package com.github.sorusclient.client.feature.impl.enhancements.v1_8_9

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.PerspectiveMode
import com.github.sorusclient.client.adapter.event.RenderCrosshairEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting

class Enhancements {

    private val centeredInventory: Setting<Boolean>
    private val hideCrossHairThirdPerson: Setting<Boolean>

    init {
        SettingManager.settingsCategory
            .apply {
                add("enhancements", CategoryData())
                    .apply {
                        data["centeredInventory"] = SettingData(Setting(false).also { centeredInventory = it })
                        data["hideCrossHairThirdPerson"] = SettingData(Setting(false).also { hideCrossHairThirdPerson = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Enhancements"))
                    .apply {
                        add(DisplayedSetting.Toggle(centeredInventory, "Centered Inventory"))
                        add(DisplayedSetting.Toggle(hideCrossHairThirdPerson, "Hide Third Person Crosshair"))
                    }
            }

        EventManager.register { event: RenderCrosshairEvent ->
            if (AdapterManager.getAdapter().perspective != PerspectiveMode.FIRST_PERSON && hideCrossHairThirdPerson.value) {
                event.canceled = true
            }
        }

        EventManager.register { event: RenderCrosshairEvent ->
            if (AdapterManager.getAdapter().perspective != PerspectiveMode.FIRST_PERSON && hideCrossHairThirdPerson.value) {
                event.canceled = true
            }
        }
    }

    fun isCenteredInventoryValue(): Boolean {
        return centeredInventory.value
    }

}