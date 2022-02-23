package com.github.sorusclient.client.plugin

import org.apache.commons.io.IOUtils
import org.json.JSONObject
import java.io.File
import java.io.IOException

object PluginManager {

    private val plugins: MutableList<Plugin> = ArrayList()

    fun findPlugins() {
        plugins.clear()
        val resources = PluginManager::class.java.classLoader.getResources("plugin.json")
        while (resources.hasMoreElements()) {
            val resource = resources.nextElement()
            val contents = IOUtils.toString(resource.openStream())
            val json = JSONObject(contents)
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