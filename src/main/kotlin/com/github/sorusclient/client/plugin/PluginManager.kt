/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.plugin

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.bootstrap.BootstrapManager
import java.io.File

object PluginManager {

    private val plugins: MutableList<Plugin> = ArrayList()

    fun findPlugins() {
        val pluginFile = File("sorus/plugin")
        pluginFile.mkdirs()

        for (plugin in pluginFile.listFiles()!!) {
            BootstrapManager.addURL(plugin.toURI().toURL())
        }

        plugins.clear()

        for (resource in PluginManager::class.java.classLoader.getResources("plugin.json")) {
            val json = BootstrapManager.loadJson(resource, AdapterManager.getAdapter().version)

            val id = json.getString("id")

            if (plugins.any { it.id == id }) continue

            val version = json.getString("version")
            val name = if (json.has("name")) json.getString("name") else id
            val description = if (json.has("description")) json.getString("description") else ""
            val logo = if (json.has("logo")) json.getString("logo") else null
            var filePath = resource.toString()
            filePath = filePath.replace("jar:file:", "")
            filePath = filePath.substring(0, filePath.indexOf("!"))
            plugins.add(Plugin(id, version, name, description, File(filePath), logo))
        }
    }

    fun remove(plugin: Plugin) {
        plugin.file.delete()
        plugins.remove(plugin)
    }

    fun getPlugins(): List<Plugin> {
        return plugins
    }

}