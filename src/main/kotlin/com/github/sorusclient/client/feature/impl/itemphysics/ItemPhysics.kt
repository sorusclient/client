package com.github.sorusclient.client.feature.impl.itemphysics

import com.github.sorusclient.client.setting.*
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting

class ItemPhysics {

    private var enabled: Setting<Boolean>

    init {
        SettingManager.settingsCategory
            .apply {
                data["itemPhysics"] = CategoryData()
                    .apply {
                        data["enabled"] = SettingData(Setting(false).also { enabled = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Item Physics")
                    .apply {
                        add(DisplayedSetting.Toggle(enabled, "Enabled"))
                    })
            }
    }

    fun isEnabled(): Boolean {
        return enabled.value
    }

}