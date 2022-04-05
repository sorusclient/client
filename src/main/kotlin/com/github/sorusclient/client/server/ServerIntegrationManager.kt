/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.server

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.GameJoinEvent
import com.github.sorusclient.client.adapter.event.GameLeaveEvent
import com.github.sorusclient.client.adapter.event.SorusCustomPacketEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.util.AssetUtil
import org.json.JSONException
import org.json.JSONObject

object ServerIntegrationManager {

    val joinListeners: MutableMap<String, (Any) -> Unit> = HashMap()
    val leaveListeners: MutableList<() -> Unit> = ArrayList()

    init {
        val eventManager = EventManager
        eventManager.register<GameJoinEvent> { onGameJoin() }
        eventManager.register<GameLeaveEvent> { onGameLeave() }
        eventManager.register(this::onCustomPacket)
    }

    private fun onGameJoin() {
        val server = AdapterManager.adapter.currentServer
        if (server != null) {
            Thread {
                val json = AssetUtil.getJsonForServer(server.ip)
                if (json != null) {
                    applyServerConfiguration(json)
                }
            }.start()
        }
    }

    private fun onGameLeave() {
        for (listener in leaveListeners) {
            listener()
        }
    }

    private fun onCustomPacket(event: SorusCustomPacketEvent) {
        if (event.channel == "integration") {
            applyServerConfiguration(event.contents)
        }
    }

    private fun applyServerConfiguration(json: String) {
        val jsonObject = JSONObject(json).toMap()
        try {
            for ((key, value) in jsonObject) {
                if (joinListeners[key] != null) {
                    joinListeners[key]?.let { it(value) }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}