/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.util

import org.apache.commons.io.IOUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.jar.JarInputStream
import java.util.zip.ZipEntry

object AssetUtil {

    private const val baseUrl = "https://raw.githubusercontent.com/sorusclient/asset/main"
    const val baseServersUrl = "$baseUrl/server"
    private const val serversJsonUrl = "$baseServersUrl/servers.json"
    const val basePluginsUrl = "$baseUrl/plugin"
    private const val pluginsJsonUrl = "$basePluginsUrl/plugins.json"

    @JvmStatic
    fun getAllServerJson(): Map<String, String> {
        val map = HashMap<String, String>()
        try {
            val inputStream = URL(serversJsonUrl).openStream()
            val jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8)
            inputStream.close()
            val json = JSONObject(jsonString).toMap()
            for ((key, _) in json) {
                val inputStream1 = URL("${baseServersUrl}/$key/metadata.json").openStream()
                val serverJson = IOUtils.toString(inputStream1, StandardCharsets.UTF_8)
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
            val jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8)
            inputStream.close()
            val json = JSONObject(jsonString).toMap()
            for ((key, value) in json) {
                if (ip.matches((value as String).toRegex())) {
                    val inputStream1 = URL("${baseServersUrl}/$key/metadata.json").openStream()
                    val serverJson = IOUtils.toString(inputStream1, StandardCharsets.UTF_8)
                    inputStream1.close()
                    return serverJson
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun getAllPlugins(): List<String> {
        val inputStream = URL(pluginsJsonUrl).openStream()
        val jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8)
        inputStream.close()
        return JSONArray(jsonString).toList() as List<String>
    }

    @JvmStatic
    fun getPluginData(id: String): Map<String, ByteArray> {
        val inputStream1 = JarInputStream(URL("${basePluginsUrl}/$id.jar").openStream())

        val map = HashMap<String, ByteArray>()

        var entry: ZipEntry? = inputStream1.nextEntry
        while (entry != null) {
            map[entry.name] = IOUtils.toByteArray(inputStream1)
            entry = inputStream1.nextEntry
        }
        inputStream1.close()

        return map
    }

}