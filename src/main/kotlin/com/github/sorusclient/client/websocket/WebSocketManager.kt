package com.github.sorusclient.client.websocket

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.TickEvent
import com.github.sorusclient.client.event.EventManager
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

object WebSocketManager {

    private lateinit var webSocket: Websocket

    val listeners: MutableMap<String, suspend (JSONObject) -> Unit> = HashMap()

    var lastConnectTime = System.currentTimeMillis()
    var connected = false
    var failedToConnect = false

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            runBlocking {
                sendMessage("updateStatus", JSONObject().apply {
                    put("version", AdapterManager.adapter.version)
                    put("action", "offline")
                })
            }
        })

        EventManager.register<TickEvent> {
            if (System.currentTimeMillis() - lastConnectTime > 10000 && !connected) {
                lastConnectTime = System.currentTimeMillis()

                Thread {
                    val session = AdapterManager.adapter.session
                    val sorusMlHash = "800D46DF17044D7033A983D9943E61CAA11835CB"

                    runBlocking {
                        try {
                            val client = HttpClient {
                                install(WebSockets)
                            }
                            client.post<String>(Url("https://sessionserver.mojang.com/session/minecraft/join")) {
                                contentType(ContentType.parse("application/json"))
                                body = "{\n" +
                                        "    \"accessToken\": \"${session.getAccessToken()}\",\n" +
                                        "    \"selectedProfile\": \"${session.getUUID()}\",\n" +
                                        "    \"serverId\": \"$sorusMlHash\"\n" +
                                        "  }\n"
                            }
                        } catch (e: Exception) {
                            System.err.println("Failed to authenticate with Mojang Session Server!")
                        }

                        webSocket = Websocket()
                        webSocket.connectBlocking()

                        val jsonObject = JSONObject()
                        jsonObject.put("username", session.getUsername())
                        jsonObject.put("uuid", session.getUUID())
                        sendMessage("authenticate", jsonObject, true)
                    }

                }.start()
            }
        }
    }

    fun sendMessage(id: String, json: JSONObject = JSONObject(), override: Boolean = false) {
        if (override || connected) {
            webSocket.send("$id $json")
        }
    }

}