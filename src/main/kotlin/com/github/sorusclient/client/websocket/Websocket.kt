package com.github.sorusclient.client.websocket

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.notification.Notification
import com.github.sorusclient.client.notification.NotificationManager
import kotlinx.coroutines.runBlocking
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.lang.Exception
import java.net.URI

class Websocket: WebSocketClient(URI.create("wss://socket.sorus.ml")) {

    override fun onOpen(handshakedata: ServerHandshake?) {
        WebSocketManager.connected = true
    }

    override fun onMessage(message: String) {
        val id = message.substring(0, message.indexOf(" "))
        val json = JSONObject(message.substring(message.indexOf(" ")))

        println("$id $json")

        if (id == "connected") {
            WebSocketManager.connected = true
            if (WebSocketManager.failedToConnect) {
                WebSocketManager.failedToConnect = false
                NotificationManager.notifications += Notification().apply {
                    title = "Websocket"
                    content = "Websocket connected!"
                }
            }

            WebSocketManager.sendMessage("updateStatus", JSONObject().apply {
                put("version", AdapterManager.getAdapter().version)
                put("action", "")
            }, true)
        }

        runBlocking {
            WebSocketManager.listeners[id]?.let { it(json) }
        }
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        println(reason)
        WebSocketManager.connected = false
    }

    override fun onError(ex: Exception) {
        ex.printStackTrace()
        WebSocketManager.connected = false
        this.close()
    }

}