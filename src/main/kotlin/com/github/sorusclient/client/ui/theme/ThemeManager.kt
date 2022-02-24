package com.github.sorusclient.client.ui.theme

import com.github.sorusclient.client.adapter.event.InitializeEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.Util
import com.github.sorusclient.client.ui.theme.default.DefaultTheme
import com.github.sorusclient.client.util.Pair
import org.apache.commons.io.IOUtils
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object ThemeManager {

    val configuredThemes: MutableList<Theme> = ArrayList()
    lateinit var currentTheme: Theme
    val registeredThemes: MutableMap<Class<out Theme>, Pair<String, String>> = HashMap()

    fun initialize() {
        registeredThemes[DefaultTheme::class.java] = Pair("Default", "sorus/ui/sorus2.png")

        val file = File("sorus/themes.json")
        if (file.exists()) {
            val json = JSONObject(IOUtils.toString(FileInputStream(file))).toMap()
            for (themeJson in json["configuredThemes"] as List<Map<String, Any>>) {
                val theme = Class.forName(themeJson["class"].toString()).getConstructor().newInstance() as Theme
                theme.category.load(themeJson, true)

                currentTheme = theme
                configuredThemes.add(theme)
            }
        } else {
            currentTheme = DefaultTheme()
            configuredThemes.add(currentTheme)
        }

        EventManager.register<InitializeEvent> {
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

            IOUtils.write(JSONObject(map).toString(2), FileOutputStream(File("sorus/themes.json")))
        })
    }

    fun registerTheme(name: String, logo: String, themeClass: Class<out Theme>) {
        registeredThemes[themeClass] = Pair(name, logo)
    }

}