package com.github.sorusclient.client.feature.impl.zoom

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.ScreenType
import com.github.sorusclient.client.adapter.event.GetFOVEvent
import com.github.sorusclient.client.adapter.event.GetSensitivityEvent
import com.github.sorusclient.client.adapter.event.GetUseCinematicCamera
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.*
import com.github.sorusclient.client.setting.display.DisplayedSetting.*
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedCategory

class Zoom {

    private var enabled: Setting<Boolean>
    private var key: Setting<out MutableList<Key>>
    private var fov: Setting<Double>
    private var sensitivity: Setting<Double>
    private var cinematicCamera: Setting<Boolean>
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
                        add(Toggle(enabled, "Enabled"))
                        add(KeyBind(key, "Key"))
                        add(Slider(fov, "FOV", 15.0, 100.0))
                        add(Slider(sensitivity, "Sensitivity", 0.25, 1.0))
                        add(Toggle(cinematicCamera, "Cinematic Camera"))
                    })
            }

        EventManager.register(this::onKey)

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
    }

    private fun onKey(event: KeyEvent) {
        if (AdapterManager.getAdapter().openScreen == ScreenType.IN_GAME) {
            if (event.key == key.value[0] && !event.isRepeat) {
                toggled = event.isPressed
            }
        }
    }

    private fun applyZoom(): Boolean {
        return enabled.value && toggled
    }

}