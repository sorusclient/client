package com.github.sorusclient.client.feature.impl.enhancements

import com.github.sorusclient.client.setting.DisplayedCategory
import com.github.sorusclient.client.setting.DisplayedSetting.Slider
import com.github.sorusclient.client.setting.DisplayedSetting.Toggle
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager

class Enhancements {

    private var enabled: Setting<Boolean>
    private val fireHeight: Setting<Double>
    private val centeredInventory: Setting<Boolean>

    init {
        SettingManager.mainCategory
            .apply {
                registerDisplayed(DisplayedCategory("Enhancements"))
                    .apply {
                        registerDisplayed(Toggle("Enabled", Setting(false).also { enabled = it }))
                        registerDisplayed(Slider("Fire Height", Setting(0.0).also { fireHeight = it }, 0.0, 1.0))
                        registerDisplayed(Toggle("Centered Inventory", Setting(false).also { centeredInventory = it }))
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