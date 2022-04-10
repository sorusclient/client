/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.social

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.IPlayerEntity
import com.github.sorusclient.client.adapter.IProfile
import com.github.sorusclient.client.adapter.event.GameJoinEvent
import com.github.sorusclient.client.adapter.event.GameLeaveEvent
import com.github.sorusclient.client.adapter.event.TickEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.notification.Icon
import com.github.sorusclient.client.notification.Interaction
import com.github.sorusclient.client.notification.Notification
import com.github.sorusclient.client.notification.display
import com.github.sorusclient.client.util.MojangUtil
import com.github.sorusclient.client.util.Pair
import com.github.sorusclient.client.util.Rectangle
import com.github.sorusclient.client.websocket.WebSocketManager
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

object SocialManager {

    var currentGroup: Group? = null
    val friends: MutableList<Pair<String, Pair<String, String>>> = ArrayList()

    val onlinePlayers: MutableSet<String> = HashSet()

    private var serverToJoin: String? = null

    init {
        WebSocketManager.listeners["acceptGroup"] = this::onAcceptGroup
        WebSocketManager.listeners["leaveGroup"] = this::onLeaveGroup
        WebSocketManager.listeners["removeGroupMember"] = this::onRemoveGroupMember
        WebSocketManager.listeners["friendRequest"] = this::onFriendRequest
        WebSocketManager.listeners["addFriend"] = this::onAddFriend
        WebSocketManager.listeners["removeFriend"] = this::onRemoveFriend
        WebSocketManager.listeners["addUserToGroup"] = this::onAddUserToGroup
        WebSocketManager.listeners["groupWarp"] = this::onWarpGroup
        WebSocketManager.listeners["updateStatus"] = this::onUpdateStatus
        WebSocketManager.listeners["requestUpdateStatus"] = this::onRequestUpdateStatus
        WebSocketManager.listeners["updateInGameStatus"] = this::onUpdateInGameStatus

        EventManager.register<TickEvent> {
            if (serverToJoin != null) {
                val adapter = AdapterManager.adapter
                adapter.leaveWorld()
                adapter.joinServer(serverToJoin!!)
                serverToJoin = null
            }
        }

        EventManager.register<GameJoinEvent> {
            val server = AdapterManager.adapter.currentServer

            if (server != null) {
                updateStatus(server.ip)
            } else {
                updateStatus("")
            }
        }

        EventManager.register<GameLeaveEvent> {
            updateStatus("")
        }

        var cachedPlayers: List<IProfile> = ArrayList()

        EventManager.register<TickEvent> {
            if (AdapterManager.adapter.world == null) return@register

            val players = AdapterManager.adapter.players
            if (players.size != cachedPlayers.size) {
                for (player in players) {
                    if (!cachedPlayers.any { it.uuid == player.uuid }) {
                        WebSocketManager.sendMessage("updateInGameStatus", JSONObject().apply {
                            put("user", player.uuid.toString().replace("-", ""))
                        })
                    }
                }
                cachedPlayers = players
            }
        }
    }

    private fun onAcceptGroup(json: JSONObject) {
        val inviter = json.getString("inviter")

        Thread {
            AdapterManager.adapter.renderer.createTexture("$inviter-skin", MojangUtil.getSkin(inviter).openStream(), false)
        }.start()

        Notification().apply {
            title = "Group Invite"
            content = "${MojangUtil.getUsername(inviter)} invited you."
            icons = listOf(Icon("$inviter-skin", Rectangle(0.125, 0.125, 0.125, 0.125)), Icon("$inviter-skin", Rectangle(0.625, 0.125, 0.125, 0.125)))

            interactions += Interaction.Button().apply {
                text = "Accept"
                onClick = {
                    runBlocking {
                        WebSocketManager.sendMessage("acceptGroup", JSONObject().apply {
                            put("inviter", inviter)
                        })
                        currentGroup = Group(false)
                        currentGroup!!.members.add(AdapterManager.adapter.session.getUUID())
                    }
                }
            }

            interactions += Interaction.Button().apply {
                text = "Deny"
            }
        }.display()
    }

    private fun onLeaveGroup(@Suppress("UNUSED_PARAMETER") json: JSONObject) {
        currentGroup = null
    }

    @Suppress()
    private fun onFriendRequest(json: JSONObject) {
        val user = json.getString("user")

        Thread {
            AdapterManager.adapter.renderer.createTexture("$user-skin", MojangUtil.getSkin(user).openStream(), false)
        }.start()

        Notification().apply {
            title = "Friend Request"
            content = "${MojangUtil.getUsername(user)} friended you."
            icons = listOf(Icon("$user-skin", Rectangle(0.125, 0.125, 0.125, 0.125)), Icon("$user-skin", Rectangle(0.625, 0.125, 0.125, 0.125)))

            interactions += Interaction.Button().apply {
                text = "Accept"
                onClick = {
                    runBlocking {
                        WebSocketManager.sendMessage("acceptFriend", JSONObject().apply {
                            put("user", user)
                        })
                        friends.add(Pair(user, Pair("", "offline")))
                    }
                }
            }

            interactions += Interaction.Button().apply {
                text = "Deny"
            }
        }.display()
    }

    private fun onAddUserToGroup(json: JSONObject) {
        currentGroup!!.members.add(json.getString("user"))
    }

    private fun onWarpGroup(json: JSONObject) {
        serverToJoin = json.getString("ip")
    }

    private fun onAddFriend(json: JSONObject) {
        friends.add(Pair(json.getString("user"), Pair("", "offline")))
    }

    private fun onRemoveFriend(json: JSONObject) {
        friends.removeIf { friend -> friend.first == json.getString("user") }
    }

    private fun onUpdateStatus(json: JSONObject) {
        val user = json.getString("user")
        for (friend in friends) {
            if (friend.first == user) {
                friend.second.first = json.getString("version")
                friend.second.second = json.getString("action")
            }
        }
    }

    private fun onUpdateInGameStatus(json: JSONObject) {
        val user = json.getString("user")
        onlinePlayers.add(user)
    }

    private fun onRequestUpdateStatus(@Suppress("UNUSED_PARAMETER") json: JSONObject) {
        val server = AdapterManager.adapter.currentServer

        if (server != null) {
            updateStatus(server.ip)
        } else {
            updateStatus("")
        }
    }

    private fun onRemoveGroupMember(json: JSONObject) {
        runBlocking {
            currentGroup!!.members.remove(json.getString("user"))
        }
    }

    fun createGroup() {
        runBlocking {
            currentGroup = Group(true)
            currentGroup!!.members.add(AdapterManager.adapter.session.getUUID())
            WebSocketManager.sendMessage("createGroup")
        }
    }

    fun invite(uuid: String) {
        if (uuid == AdapterManager.adapter.session.getUUID()) return

        runBlocking {
            WebSocketManager.sendMessage("inviteToGroup", JSONObject().apply {
                put("user", uuid)
            })
        }
    }

    fun sendFriend(uuid: String) {
        if (uuid == AdapterManager.adapter.session.getUUID()) return

        runBlocking {
            WebSocketManager.sendMessage("sendFriend", JSONObject().apply {
                put("user", uuid)
            })
        }
    }

    fun unfriend(uuid: String) {
        friends.removeIf { friend -> friend.first == uuid }
        runBlocking {
            WebSocketManager.sendMessage("unfriend", JSONObject().apply {
                put("user", uuid)
            })
        }
    }

    fun warpGroup() {
        runBlocking {
            WebSocketManager.sendMessage("groupWarp", JSONObject().apply {
                put("ip", AdapterManager.adapter.currentServer!!.ip)
            })
        }
    }

    private fun updateStatus(action: String) {
        runBlocking {
            WebSocketManager.sendMessage("updateStatus", JSONObject().apply {
                put("action", action)
                put("version", AdapterManager.adapter.version)
            })
        }
    }

    fun disbandGroup() {
        runBlocking {
            WebSocketManager.sendMessage("disbandGroup")
        }
    }

    fun removeGroupMember(uuid: String) {
        runBlocking {
            WebSocketManager.sendMessage("removeGroupMember", JSONObject().apply {
                put("user", uuid)
            })
            currentGroup!!.members.remove(uuid)
        }
    }

}