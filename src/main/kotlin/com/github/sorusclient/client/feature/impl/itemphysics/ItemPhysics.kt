/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.itemphysics

import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting

object ItemPhysics {

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