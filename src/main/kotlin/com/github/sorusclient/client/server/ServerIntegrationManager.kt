package com.github.sorusclient.client.server

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.GameJoinEvent
import com.github.sorusclient.client.adapter.event.GameLeaveEvent
import com.github.sorusclient.client.adapter.event.SorusCustomPacketEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.module.ModuleManager
import org.apache.commons.io.IOUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL

object ServerIntegrationManager {

    private const val baseServersUrl = "https://raw.githubusercontent.com/sorusclient/asset/main/server"
    private const val serversJsonUrl = "$baseServersUrl/servers.json"

    val listeners: MutableMap<String, (Any) -> Unit> = HashMap()

    init {
        val eventManager = EventManager
        eventManager.register(this::onGameJoin)
        eventManager.register(this::onGameLeave)
        eventManager.register(this::onCustomPacket)

        listeners["module"] = this::handleModule
    }

    private fun onGameJoin(event: GameJoinEvent) {
        val server = AdapterManager.getAdapter().currentServer
        if (server != null) {
            Thread {
                val json = getJsonForServer(server.ip)
                if (json != null) {
                    applyServerConfiguration(json)
                }
            }.start()
        }
    }

    private fun onGameLeave(event: GameLeaveEvent) {
        removeServerConfiguration()
    }

    private fun onCustomPacket(event: SorusCustomPacketEvent) {
        if (event.channel == "integration") {
            applyServerConfiguration(event.contents)
        }
    }

    private fun getJsonForServer(ip: String): String? {
        try {
            val inputStream = URL(serversJsonUrl).openStream()
            val jsonString = IOUtils.toString(inputStream)
            inputStream.close()
            val json = JSONObject(jsonString).toMap()
            for ((key, value) in json) {
                if (ip.matches((value as String).toRegex())) {
                    val inputStream1 = URL("$baseServersUrl/$key.json").openStream()
                    val serverJson = IOUtils.toString(inputStream1)
                    inputStream1.close()
                    return serverJson
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun applyServerConfiguration(json: String) {
        val jsonObject = JSONObject(json).toMap()
        try {
            for ((key, value) in jsonObject) {
                if (listeners[key] != null) {
                    listeners[key]?.let { it(value) }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun handleModule(json: Any) {
        val modules = json as Map<String, Any>
        for ((key, value) in modules) {
            val module = ModuleManager[key]
            module!!.loadForced(value as Map<String, Any>)
        }
    }

    private fun removeServerConfiguration() {
        for (moduleData in ModuleManager.modules.values) {
            val module = moduleData.module
            module.removeForced()
        }
    }

}