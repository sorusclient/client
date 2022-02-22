package com.github.sorusclient.client.util

import com.github.sorusclient.client.server.ServerIntegrationManager
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import java.io.IOException
import java.net.URL

object AssetUtil {

    private const val baseServersUrl = "https://raw.githubusercontent.com/sorusclient/asset/main/server"
    private const val serversJsonUrl = "$baseServersUrl/servers.json"

    @JvmStatic
    fun getAllServerJson(): List<String> {
        val list = ArrayList<String>()
        try {
            val inputStream = URL(serversJsonUrl).openStream()
            val jsonString = IOUtils.toString(inputStream)
            inputStream.close()
            val json = JSONObject(jsonString).toMap()
            for ((key, _) in json) {
                val inputStream1 = URL("${baseServersUrl}/$key.json").openStream()
                val serverJson = IOUtils.toString(inputStream1)
                inputStream1.close()
                list.add(serverJson)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return list
    }

    @JvmStatic
    fun getJsonForServer(ip: String): String? {
        try {
            val inputStream = URL(serversJsonUrl).openStream()
            val jsonString = IOUtils.toString(inputStream)
            inputStream.close()
            val json = JSONObject(jsonString).toMap()
            for ((key, value) in json) {
                if (ip.matches((value as String).toRegex())) {
                    val inputStream1 = URL("${baseServersUrl}/$key.json").openStream()
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

}