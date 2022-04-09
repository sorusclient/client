/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.autogg

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.ChatReceivedEvent
import com.github.sorusclient.client.bootstrap.server.ServerIntegrationManager
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.*
import com.github.sorusclient.client.setting.display.DisplayedSetting.*
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedCategory

object AutoGG {

    private var enabled: Setting<Boolean>
    private var autoggTriggers: MutableList<Regex> = ArrayList()
    private var command: String? = null

    init {
        SettingManager.settingsCategory
            .apply {
                data["autogg"] = CategoryData()
                    .apply {
                        data["enabled"] = SettingData(Setting(false).also { enabled = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("AutoGG")
                    .apply {
                        add(Toggle(enabled, "Enabled"))
                    })
            }

        ServerIntegrationManager.registerJoinListener("autogg") { json ->
            val autogg = json as HashMap<*, *>
            val triggers = autogg["triggers"] as List<*>
            autoggTriggers.clear()
            autoggTriggers.addAll(triggers.map { string ->
                string.toString().toRegex()
            }.toList())

            command = autogg["command"] as String
        };
    }

    private fun isAutoGGTrigger(message: String): Boolean {
        for (trigger in autoggTriggers) {
            if (trigger.containsMatchIn(message)) {
                return true
            }
        }
        return false
    }

}