/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.oldanimations.v1_8_9

import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting.Toggle
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData

object OldAnimations {

    private var oldBlockHit: Setting<Boolean>

    init {
        SettingManager.settingsCategory
            .apply {
                add("oldAnimations", CategoryData())
                    .apply {
                        data["oldBlockHit"] = SettingData(Setting(false).also { oldBlockHit = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Old Animations"))
                    .apply {
                        add(Toggle(oldBlockHit, "Old Block Hit"))
                    }
            }
    }

    fun isOldBlockHitValue(): Boolean {
        return oldBlockHit.value
    }

}