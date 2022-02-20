package com.github.sorusclient.client.feature.impl.itemphysics

import com.github.sorusclient.client.setting.*

class ItemPhysics {

    private var enabled: Setting<Boolean>

    init {
        SettingManager.settingsCategory
            .apply {
                put("itemPhysics", HashMap<String, Any>()
                    .apply {
                        put("enabled", Setting(false).also { enabled = it })
                    })
            }

        SettingManager.mainUICategory
            .apply {
                add(Category("Item Physics")
                    .apply {
                        add(SettingConfigure.Toggle(enabled, "Enabled"))
                    })
            }
    }

    fun isEnabled(): Boolean {
        return enabled.value
    }

}