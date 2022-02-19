package com.github.sorusclient.client.feature.impl.autogg

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.ChatReceivedEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.server.ServerIntegrationManager
import com.github.sorusclient.client.setting.DisplayedCategory
import com.github.sorusclient.client.setting.DisplayedSetting
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager

class AutoGG {

    private var enabled: Setting<Boolean>
    private var autoggTriggers: MutableList<Regex> = ArrayList()
    private var command: String? = null

    init {
        SettingManager.mainCategory
            .apply {
                registerDisplayed(DisplayedCategory("AutoGG"))
                    .apply {
                        registerDisplayed(DisplayedSetting.Toggle("Enabled", Setting(false).also { enabled = it }))
                    }
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