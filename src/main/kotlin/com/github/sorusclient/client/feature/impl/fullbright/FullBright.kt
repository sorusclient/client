package com.github.sorusclient.client.feature.impl.fullbright

import com.github.sorusclient.client.adapter.event.GetGammaEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.DisplayedSetting.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager

class FullBright {

    private var enabled: Setting<Boolean>

    init {
        SettingManager.mainCategory
            .apply {
                registerDisplayed(Toggle("FullBright", Setting(false).also { enabled = it }))
            }
        EventManager.register<GetGammaEvent> { event ->
            if (enabled.value) {
                event.gamma = 100.0
            }
        }
    }

}