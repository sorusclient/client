package com.github.sorusclient.client.websocket

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.TickEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.notification.Icon
import com.github.sorusclient.client.notification.Notification
import com.github.sorusclient.client.notification.NotificationManager
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

object WebSocketManager {

    private lateinit var webSocket: ClientWebSocketSession

    val listeners: MutableMap<String, suspend (JSONObject) -> Unit> = HashMap()

    var lastConnectTime = System.currentTimeMillis()
    var connected = false
    var failedToConnect = false

    init {
        EventManager.register<TickEvent> {
            if (System.currentTimeMillis() - lastConnectTime > 10000 && !connected) {
                lastConnectTime = System.currentTimeMillis()

                Thread {
                    val client = HttpClient {
                        install(WebSockets)
                    }

                    runBlocking {
                        val session = AdapterManager.getAdapter().session
                        val sorusMlHash = "800D46DF17044D7033A983D9943E61CAA11835CB"

                        try {
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

                        try {
                            client.webSocket(method = HttpMethod.Get, host = "localhost", port = 8080, path = "/") {
                                webSocket = this
                                val jsonObject = JSONObject()
                                jsonObject.put("username", session.getUsername())
                                jsonObject.put("uuid", session.getUUID())
                                sendMessage("authenticate", jsonObject)

                                while (true) {
                                    if (incoming.isClosedForReceive) return@webSocket
                                    val othersMessage = incoming.receive() as? Frame.Text ?: continue

                                    val message = othersMessage.readText()
                                    val id = message.substring(0, message.indexOf(" "))
                                    val json = message.substring(message.indexOf(" "))

                                    onReceiveMessage(id, JSONObject(json))
                                }
                            }
                        } catch(e: Exception) {
                            //e.printStackTrace()
                        }
                    }

                    client.close()
                    connected = false

                    if (!failedToConnect) {
                        failedToConnect = true
                        NotificationManager.notifications += Notification().apply {
                            title = "Websocket"
                            content = "Websocket failed to connect."

                            subIcon = Icon("sorus/ui/error.png")
                        }
                    }
                }.start()
            }
        }
    }

    suspend fun sendMessage(id: String, json: JSONObject = JSONObject()) {
        webSocket.send("$id $json")
    }

    private suspend fun onReceiveMessage(id: String, json: JSONObject) {
        println("$id $json")

        if (id == "connected") {
            connected = true
            if (failedToConnect) {
                failedToConnect = false
                NotificationManager.notifications += Notification().apply {
                    title = "Websocket"
                    content = "Websocket connected!"
                }
                println("test")
            }
        }

        listeners[id]?.let { it(json) }
    }

}