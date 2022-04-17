/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.websocket

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.notification.Icon
import com.github.sorusclient.client.notification.Notification
import com.github.sorusclient.client.notification.display
import kotlinx.coroutines.runBlocking
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI

class Websocket: WebSocketClient(URI.create(if (System.getProperty("sorus.test") == "true") { "ws://localhost:3000" } else { "wss://socket.sorus.ml:8443" })) {

    override fun onOpen(handshakedata: ServerHandshake?) {

    }

    override fun onMessage(message: String) {
        val id = message.substring(0, message.indexOf(" "))
        val json = JSONObject(message.substring(message.indexOf(" ")))

        println("$id $json")

        if (id == "connected") {
            if (WebSocketManager.failedToConnect) {
                WebSocketManager.failedToConnect = false
                Notification().apply {
                    title = "Websocket"
                    content = "Websocket connected!"
                }.display()
            }

            WebSocketManager.connected = true

            WebSocketManager.sendMessage("updateStatus", JSONObject().apply {
                put("version", AdapterManager.adapter.version)
                put("action", "")
            })
        }

        runBlocking {
            WebSocketManager.listeners[id]?.let { it(json) }
        }
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        println(reason)

        if (!WebSocketManager.failedToConnect && WebSocketManager.connected) {
            Notification().apply {
                title = "Websocket"
                content = "Websocket disconnected!"
                subIcon = Icon("sorus/ui/error.png")
            }.display()
        }

        WebSocketManager.connected = false
    }

    override fun onError(ex: Exception) {
        ex.printStackTrace()
        WebSocketManager.connected = false
        this.close()
    }

}