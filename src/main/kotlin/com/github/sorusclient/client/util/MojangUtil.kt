package com.github.sorusclient.client.util

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.util.Base64

object MojangUtil {

    private val httpClient = HttpClient()

    private val uuidToUsername: BiMap<String, String> = HashBiMap.create()
    private val uuidToSkin: MutableMap<String, URL> = HashMap()

    fun getUsername(uuid: String): String {
        if (uuidToUsername.containsKey(uuid)) return uuidToUsername[uuid]!!

        val username = runBlocking {
            try {
                val userResponse = JSONArray(httpClient.get<String>("https://api.mojang.com/user/profiles/$uuid/names"))
                userResponse.getJSONObject(0).getString("name")
            } catch(e: Exception) {
                uuid.substring(0, 16)
            }
        }
        uuidToUsername[uuid] = username
        return username
    }

    fun getUUID(username: String): String {
        if (uuidToUsername.inverse().containsKey(username)) return uuidToUsername.inverse()[username]!!

        val uuid = runBlocking {
            val userResponse = JSONObject(httpClient.get<String>("https://api.mojang.com/users/profiles/minecraft/$username"))
            userResponse.getString("id")
        }
        uuidToUsername[uuid] = username
        return uuid
    }

    fun getSkin(uuid: String): URL {
        if (uuidToSkin.containsKey(uuid)) return uuidToSkin[uuid]!!

        val skinURL = URL(runBlocking {
            try {
                val userResponse = JSONObject(httpClient.get<String>("https://sessionserver.mojang.com/session/minecraft/profile/$uuid"))
                val textures = JSONObject(String(Base64.getDecoder().decode(userResponse.getJSONArray("properties").getJSONObject(0).getString("value"))))
                textures.getJSONObject("textures").getJSONObject("SKIN").getString("url")
            } catch (e: Exception) {
                ""
            }
        })
        uuidToSkin[uuid] = skinURL
        return skinURL
    }

}