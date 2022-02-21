package com.github.sorusclient.client.feature.impl.autogg

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.ChatReceivedEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.server.ServerIntegrationManager
import com.github.sorusclient.client.setting.*
import com.github.sorusclient.client.setting.display.DisplayedSetting.*
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedCategory

class AutoGG {

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

        ServerIntegrationManager.joinListeners["autogg"] = { json ->
            val autogg = json as HashMap<*, *>
            val triggers = autogg["triggers"] as List<*>
            autoggTriggers.clear()
            println(triggers)
            autoggTriggers.addAll(triggers.map { string ->
                println(string)
                string.toString().toRegex()
            }.toList())

            command = autogg["command"] as String
        }

        EventManager.register { event: ChatReceivedEvent ->
            if (enabled.value) {
                if (isAutoGGTrigger(event.message)) {
                    AdapterManager.getAdapter().sendPlayerMessage("gg")
                }
            }
        }
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