/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.ui.theme

import com.github.sorusclient.client.adapter.event.InitializeEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.ui.framework.Container
import com.github.sorusclient.client.ui.framework.ContainerRenderer
import com.github.sorusclient.client.ui.theme.default.DefaultTheme
import com.github.sorusclient.client.util.Pair
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

object ThemeManager {

    val configuredThemes: MutableList<Theme> = ArrayList()
    val registeredThemes: MutableMap<Class<out Theme>, Pair<String, String>> = HashMap()

    fun initialize() {
        registeredThemes[DefaultTheme::class.java] = Pair("Default", "sorus/ui/sorus2.png")

        val file = File("sorus/themes.json")
        if (file.exists()) {
            val json = JSONObject(IOUtils.toString(FileInputStream(file), StandardCharsets.UTF_8)).toMap()
            for (themeJson in json["configuredThemes"] as List<Map<String, Any>>) {
                val theme = Class.forName(themeJson["class"].toString()).getConstructor().newInstance() as Theme
                theme.category.load(themeJson, true)

                configuredThemes.add(theme)
            }
        } else {
            configuredThemes.add(DefaultTheme())
        }

        EventManager.register(InitializeEvent::class.java) {
            for (theme in configuredThemes) {
                theme.initialize()
            }
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            val map = HashMap<String, Any>()
            val configuredThemes = ArrayList<Any>()
            map["configuredThemes"] = configuredThemes
            for (theme in this.configuredThemes) {
                val themeMap = theme.category.save() as HashMap<String, Any>
                themeMap["class"] = theme.javaClass.name
                configuredThemes.add(themeMap)
            }

            IOUtils.write(JSONObject(map).toString(2), FileOutputStream(File("sorus/themes.json")), StandardCharsets.UTF_8)
        })
    }

    fun registerTheme(name: String, logo: String, themeClass: Class<out Theme>) {
        registeredThemes[themeClass] = Pair(name, logo)
    }

    private val openedGuis: MutableList<Container> = mutableListOf()

    fun open(id: String, vararg arguments: Any) {
        for (theme in configuredThemes) {
            val container = theme.items[id]
            if (container != null) {
                ContainerRenderer.open(container)
                theme.onOpenGui(id, *arguments)
                openedGuis += container
                return
            }
        }

        error("GUI $id does not have any themes!")
    }

    fun openMainGui() {
        open("mainGui")
    }

    fun openSettingsGui(category: DisplayedCategory) {
        open("mainGui", category)
    }

    fun openMenuGui(menu: String) {
        open("mainGui", menu)
    }

    fun closeGui() {
        for (gui in openedGuis) {
            ContainerRenderer.close(gui)
        }
        for (theme in configuredThemes) {
            theme.closeGui()
        }
    }

    fun openSearchGui() {
        open("searchGui")
    }

}