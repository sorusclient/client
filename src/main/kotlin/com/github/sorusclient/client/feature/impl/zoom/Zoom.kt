package com.github.sorusclient.client.feature.impl.zoom

import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.event.GetFOVEvent
import com.github.sorusclient.client.adapter.event.GetSensitivityEvent
import com.github.sorusclient.client.adapter.event.GetUseCinematicCamera
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.*
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting
import com.github.sorusclient.client.util.keybind.KeyBind
import com.github.sorusclient.client.util.keybind.KeyBindManager

class Zoom {

    private val enabled: Setting<Boolean>
    private val key: Setting<out MutableList<Key>>
    private val fov: Setting<Double>
    private val sensitivity: Setting<Double>
    private val cinematicCamera: Setting<Boolean>

    private var toggled = false

    init {
        SettingManager.settingsCategory
            .apply {
                data["zoom"] = CategoryData()
                        .apply {
                            data["enabled"] = SettingData(Setting(false).also { enabled = it })
                            data["key"] = SettingData(Setting(arrayListOf(Key.C)).also { key = it })
                            data["fov"] = SettingData(Setting(30.0).also { fov = it })
                            data["sensitivity"] = SettingData(Setting(0.5).also { sensitivity = it })
                            data["cinematicCamera"] = SettingData(Setting(false).also { cinematicCamera = it })
                        }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Zoom")
                    .apply {
                        add(DisplayedSetting.Toggle(enabled, "Enabled"))
                        add(DisplayedSetting.KeyBind(key, "Key"))
                        add(DisplayedSetting.Slider(fov, "FOV", 15.0, 100.0))
                        add(DisplayedSetting.Slider(sensitivity, "Sensitivity", 0.25, 1.0))
                        add(DisplayedSetting.Toggle(cinematicCamera, "Cinematic Camera"))
                    })
            }

        EventManager.register<GetFOVEvent> { event ->
            if (applyZoom()) {
                event.fov = fov.value
            }
        }

        EventManager.register<GetSensitivityEvent> { event ->
            if (applyZoom()) {
                event.sensitivity = event.sensitivity * sensitivity.value
            }
        }

        EventManager.register<GetUseCinematicCamera> { event ->
            if (applyZoom()) {
                event.useCinematicCamera = cinematicCamera.value
            }
        }

        KeyBindManager.register(KeyBind(
            {
                key.value
            },
            this::onKeyUpdate
        ))
    }

    private fun onKeyUpdate(pressed: Boolean) {
        toggled = pressed
    }

    private fun applyZoom(): Boolean {
        return enabled.value && toggled
    }

}