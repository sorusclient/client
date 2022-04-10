/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.oldanimations

import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting.Toggle

object OldAnimations {

    private var showArmorDamage: Setting<Boolean>

    init {
        SettingManager.settingsCategory
            .apply {
                add("oldAnimations", CategoryData())
                    .apply {
                        data["showArmorDamage"] = SettingData(Setting(false).also { showArmorDamage = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Old Animations"))
                    .apply {
                        add(Toggle(showArmorDamage, "Show Armor Damage"))
                    }
            }
    }

    fun showArmorDamageValue(): Boolean {
        return showArmorDamage.value
    }

}