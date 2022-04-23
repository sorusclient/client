/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.enhancements

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.event.GetGammaEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.Util
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting.*
import org.apache.commons.io.FileUtils
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets

object Enhancements {

    private val fireHeight: Setting<Double>
    private val partialViewBobbing: Setting<Boolean>
    private val fullBright: Setting<Boolean>

    init {
        SettingManager.settingsCategory
            .apply {
                add("enhancements", CategoryData())
                    .apply {
                        data["fullBright"] = SettingData(Setting(false).also { fullBright = it })
                        data["fireHeight"] = SettingData(Setting(0.0).also { fireHeight = it })
                        data["partialViewBobbing"] = SettingData(Setting(false).also { partialViewBobbing = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Enhancements"))
                    .apply {
                        add(Toggle(fullBright, "FullBright"))
                        add(Slider(fireHeight, "Fire Height", 0.0, 1.0))
                        add(Toggle(partialViewBobbing, "Partial View Bobbing"))
                    }
            }

        EventManager.register<GetGammaEvent> { event ->
            if (fullBright.value) {
                event.gamma = 100.0
            }
        }
    }

    fun getFireHeightValue(): Double {
        return fireHeight.value
    }

    private var settings: Map<String, Any> = HashMap()
    private var settingsLoader: ISettingsLoader = InterfaceManager.get()

    fun saveSettings() {
        val jsonObject = settingsLoader.save(settings)
        settings = jsonObject
        val settingsFile = File("sorus/settings.json")
        FileUtils.writeStringToFile(settingsFile, JSONObject(Util.toData(settings) as Map<*, *>).toString(2), StandardCharsets.UTF_8)
    }

    fun loadSettings() {
        val settingsFile = File("sorus/settings.json")
        if (!settingsFile.exists()) return

        val contents = FileUtils.readFileToString(settingsFile, StandardCharsets.UTF_8)
        val baseMap = JSONObject(contents).toMap()

        val map: MutableMap<String, Any> = HashMap()

        for ((key, value) in baseMap) {
            when (key) {
                "sensitivity" -> map[key] = Util.toJava(Double::class.java, value)!!
                "chat" -> map[key] = Util.toJava(Key::class.java, value)!!
                "command" -> map[key] = Util.toJava(Key::class.java, value)!!
                "sprint" -> map[key] = Util.toJava(Key::class.java, value)!!
                "sneak" -> map[key] = Util.toJava(Key::class.java, value)!!
                "perspective" -> map[key] = Util.toJava(Key::class.java, value)!!
                "socialInteractions" -> map[key] = Util.toJava(Key::class.java, value)!!
                "graphics" -> map[key] = Util.toJava(Graphics::class.java, value)!!
                "autoJump" -> map[key] = value
                "attackIndicator" -> map[key] = Util.toJava(AttackIndicator::class.java, value)!!
                "skipMultiplayerWarning" -> map[key] = value
                "rawInput" -> map[key] = value
                else -> {
                    if (key.startsWith("hotbar_")) {
                        map[key] = Util.toJava(Key::class.java, value)!!
                    }
                }
            }
        }

        settingsLoader.load(map)
        settings = map
    }

    enum class Graphics {
        FAST,
        FANCY,
        FABULOUS
    }

    enum class AttackIndicator {
        OFF,
        CROSSHAIR,
        HOTBAR
    }

    fun isPartialViewBobbing(): Boolean {
        return partialViewBobbing.value
    }

}