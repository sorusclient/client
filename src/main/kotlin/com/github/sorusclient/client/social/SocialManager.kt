package com.github.sorusclient.client.social

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.TickEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.notification.Icon
import com.github.sorusclient.client.notification.Interaction
import com.github.sorusclient.client.notification.Notification
import com.github.sorusclient.client.notification.NotificationManager
import com.github.sorusclient.client.util.MojangUtil
import com.github.sorusclient.client.websocket.WebSocketManager
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

object SocialManager {

    var currentGroup: Group? = null

    private var serverToJoin: String? = null

    init {
        WebSocketManager.listeners["acceptGroup"] = this::onAcceptGroup
        WebSocketManager.listeners["addUserToGroup"] = this::onAddUserToGroup
        WebSocketManager.listeners["groupWarp"] = this::onWarpGroup

        EventManager.register<TickEvent> {
            if (serverToJoin != null) {
                val adapter = AdapterManager.getAdapter()
                adapter.leaveWorld()
                adapter.joinServer(serverToJoin!!)
                serverToJoin = null
            }
        }
    }

    private suspend fun onAcceptGroup(json: JSONObject) {
        val inviter = "d0a684fdcbe445d5abb39990ae1cfc3a"

        Thread {
            AdapterManager.getAdapter().renderer.createTexture("$inviter-skin", MojangUtil.getSkin(inviter).openStream(), false)
        }.start()

        NotificationManager.notifications += Notification().apply {
            title = "Group Invite"
            content = "${MojangUtil.getUsername(inviter)} invited you."
            icons = listOf(Icon("$inviter-skin", arrayOf(0.125, 0.125, 0.125, 0.125)), Icon("$inviter-skin", arrayOf(0.625, 0.125, 0.125, 0.125)))

            interactions += Interaction.Button().apply {
                text = "Accept"
                onClick = {
                    runBlocking {
                        WebSocketManager.sendMessage("acceptGroup", JSONObject().apply {
                            put("inviter", json.getString("inviter"))
                        })
                        currentGroup = Group(false)
                        currentGroup!!.members.add(AdapterManager.getAdapter().session.getUUID())
                    }
                }
            }

            interactions += Interaction.Button().apply {
                text = "Deny"
            }
        }
    }

    private suspend fun onAddUserToGroup(json: JSONObject) {
        currentGroup!!.members.add(json.getString("user"))
    }

    private suspend fun onWarpGroup(json: JSONObject) {
        serverToJoin = json.getString("ip")
    }

    fun createGroup() {
        runBlocking {
            currentGroup = Group(true)
            currentGroup!!.members.add(AdapterManager.getAdapter().session.getUUID())
            WebSocketManager.sendMessage("createGroup")
        }
    }

    fun invite(uuid: String) {
        if (uuid == AdapterManager.getAdapter().session.getUUID()) return

        runBlocking {
            WebSocketManager.sendMessage("inviteToGroup", JSONObject().apply {
                put("user", uuid)
            })
        }
    }

    fun warpGroup() {
        runBlocking {
            WebSocketManager.sendMessage("groupWarp", JSONObject().apply {
                put("ip", AdapterManager.getAdapter().currentServer!!.ip)
            })
        }
    }

}