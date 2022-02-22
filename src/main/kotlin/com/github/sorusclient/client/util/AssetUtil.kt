package com.github.sorusclient.client.util

import org.apache.commons.io.IOUtils
import org.json.JSONObject
import java.io.IOException
import java.net.URL

object AssetUtil {

    const val baseServersUrl = "https://raw.githubusercontent.com/sorusclient/asset/main/server"
    private const val serversJsonUrl = "$baseServersUrl/servers.json"

    @JvmStatic
    fun getAllServerJson(): Map<String, String> {
        val map = HashMap<String, String>()
        try {
            val inputStream = URL(serversJsonUrl).openStream()
            val jsonString = IOUtils.toString(inputStream)
            inputStream.close()
            val json = JSONObject(jsonString).toMap()
            for ((key, _) in json) {
                val inputStream1 = URL("${baseServersUrl}/$key/metadata.json").openStream()
                val serverJson = IOUtils.toString(inputStream1)
                inputStream1.close()
                map[key] = serverJson
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return map
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
                    val inputStream1 = URL("${baseServersUrl}/$key/metadata.json").openStream()
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