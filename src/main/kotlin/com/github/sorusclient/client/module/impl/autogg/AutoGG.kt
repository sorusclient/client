package com.github.sorusclient.client.module.impl.autogg

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.ChatReceivedEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.module.ModuleDisableable
import com.github.sorusclient.client.server.ServerIntegrationManager

class AutoGG: ModuleDisableable("autogg") {

    private var autoggTriggers: MutableList<Regex> = ArrayList()
    private var command: String? = null

    init {
        ServerIntegrationManager.listeners["autogg"] = { json ->
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
            if (isEnabled()) {
                if (isAutoGGTrigger(event.message)) {
                    AdapterManager.getAdapter().sendPlayerMessage("gg")
                }
            }
        }
    }

    private fun isAutoGGTrigger(message: String): Boolean {
        for (trigger in autoggTriggers) {
            //println(trigger)
            if (trigger.containsMatchIn(message)) {
                return true
            }
        }
        return false
    }

}