/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.fov

import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting.*

object FOV {

    private val sprintingEffect: Setting<Double>
    private val potionEffect: Setting<Double>

    init {
        SettingManager.settingsCategory
            .apply {
                add("fov", CategoryData())
                    .apply {
                        data["sprintingEffect"] = SettingData(Setting(1.0).also { sprintingEffect = it })
                        data["potionEffect"] = SettingData(Setting(1.0).also { potionEffect = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("FOV"))
                    .apply {
                        add(Slider(sprintingEffect, "Sprinting Effect", 0.0, 1.0))
                        add(Slider(potionEffect, "Potion Effect", 0.0, 1.0))
                    }
            }
    }

    fun getSprintingEffect(): Double {
        return sprintingEffect.value
    }

    fun getPotionEffect(): Double {
        return potionEffect.value
    }

}