package com.github.sorusclient.client.feature.impl.fullbright

import com.github.sorusclient.client.adapter.event.GetGammaEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.display.DisplayedSetting.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.SettingData

object FullBright {

    private var enabled: Setting<Boolean>

    init {
        SettingManager.settingsCategory
            .apply {
                data["fullBright"] = SettingData(Setting(false).also { enabled = it })
            }

        SettingManager.mainUICategory
            .apply {
                add(Toggle(enabled, "FullBright"))
            }

        EventManager.register<GetGammaEvent> { event ->
            if (enabled.value) {
                event.gamma = 100.0
            }
        }
    }

}