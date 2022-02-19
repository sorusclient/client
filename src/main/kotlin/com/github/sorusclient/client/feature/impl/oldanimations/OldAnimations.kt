package com.github.sorusclient.client.feature.impl.oldanimations

import com.github.sorusclient.client.setting.DisplayedCategory
import com.github.sorusclient.client.setting.DisplayedSetting.Toggle
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager

class OldAnimations {

    private var enabled: Setting<Boolean>
    private var oldBlockHit: Setting<Boolean>
    private var showArmorDamage: Setting<Boolean>

    init {
        SettingManager.mainCategory
            .apply {
                registerDisplayed(DisplayedCategory("Old Animations"))
                    .apply {
                        registerDisplayed(Toggle("Enabled", Setting(false).also { enabled = it }))
                        registerDisplayed(Toggle("Old Block Hit", Setting(false).also { oldBlockHit = it }))
                        registerDisplayed(Toggle("Show Armor Damage", Setting(false).also { showArmorDamage = it }))
                    }
            }
    }

    fun isOldBlockHitValue(): Boolean {
        return oldBlockHit.value
    }

    fun showArmorDamageValue(): Boolean {
        return showArmorDamage.value
    }

    fun isEnabled(): Boolean {
        return enabled.value
    }

}