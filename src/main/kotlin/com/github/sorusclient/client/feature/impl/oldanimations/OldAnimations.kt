package com.github.sorusclient.client.feature.impl.oldanimations

import com.github.sorusclient.client.setting.Category
import com.github.sorusclient.client.setting.SettingConfigure.Toggle
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager

class OldAnimations {

    private var enabled: Setting<Boolean>
    private var oldBlockHit: Setting<Boolean>
    private var showArmorDamage: Setting<Boolean>

    init {
        SettingManager.settingsCategory
            .apply {
                put("oldAnimations", HashMap<String, Any>()
                    .apply {
                        put("enabled", Setting(false).also { enabled = it })
                        put("oldBlockHit", Setting(false).also { oldBlockHit = it })
                        put("showArmorDamage", Setting(false).also { showArmorDamage = it })
                    })
            }

        SettingManager.mainUICategory
            .apply {
                add(Category("Old Animations"))
                    .apply {
                        add(Toggle(enabled, "Enabled"))
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

    fun isEnabled(): Boolean {
        return enabled.value
    }

}