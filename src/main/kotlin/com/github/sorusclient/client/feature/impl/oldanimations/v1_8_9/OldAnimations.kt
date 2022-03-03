package com.github.sorusclient.client.feature.impl.oldanimations.v1_8_9

import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting.Toggle
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData

class OldAnimations {

    private var oldBlockHit: Setting<Boolean>
    private var showArmorDamage: Setting<Boolean>

    init {
        SettingManager.settingsCategory
            .apply {
                data["oldAnimations"] = CategoryData()
                    .apply {
                        data["oldBlockHit"] = SettingData(Setting(false).also { oldBlockHit = it })
                        data["showArmorDamage"] = SettingData(Setting(false).also { showArmorDamage = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Old Animations"))
                    .apply {
                        add(Toggle(oldBlockHit, "Old Block Hit"))
                        add(Toggle(showArmorDamage, "Show Armor Damage"))
                    }
            }
    }

    fun isOldBlockHitValue(): Boolean {
        return oldBlockHit.value
    }

    fun showArmorDamageValue(): Boolean {
        return showArmorDamage.value
    }

}