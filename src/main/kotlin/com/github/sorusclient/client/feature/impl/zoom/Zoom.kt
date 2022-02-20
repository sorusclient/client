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
import com.github.sorusclient.client.setting.SettingConfigure.*

class Zoom {

    private var enabled: Setting<Boolean>
    private var key: Setting<Key>
    private var fov: Setting<Double>
    private var sensitivity: Setting<Double>
    private var cinematicCamera: Setting<Boolean>
    private var toggled = false

    init {
        SettingManager.settingsCategory
            .apply {
                put("zoom", HashMap<String, Any>()
                        .apply {
                            put("enabled", Setting(false).also { enabled = it })
                            put("key", Setting(Key.C).also { key = it })
                            put("fov", Setting(30.0).also { fov = it })
                            put("sensitivity", Setting(0.5).also { sensitivity = it })
                            put("cinematicCamera", Setting(false).also { cinematicCamera = it })
                        })
            }

        SettingManager.mainUICategory
            .apply {
                add(Category("Zoom")
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
            if (event.key == key.value && !event.isRepeat) {
                toggled = event.isPressed
            }
        }
    }

    private fun applyZoom(): Boolean {
        return enabled.value && toggled
    }

}