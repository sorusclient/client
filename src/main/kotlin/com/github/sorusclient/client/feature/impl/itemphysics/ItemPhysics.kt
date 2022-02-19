package com.github.sorusclient.client.feature.impl.itemphysics

import com.github.sorusclient.client.setting.DisplayedCategory
import com.github.sorusclient.client.setting.DisplayedSetting
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager

class ItemPhysics {

    private var enabled: Setting<Boolean>

    init {
        SettingManager.mainCategory
            .apply {
                registerDisplayed(DisplayedCategory("ItemPhysics"))
                    .apply {
                        registerDisplayed(DisplayedSetting.Toggle("Enabled", Setting(false).also { enabled = it }))
                    }
            }
    }

    fun isEnabled(): Boolean {
        return enabled.value
    }

}