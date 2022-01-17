package com.github.sorusclient.client.module.impl.zoom

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.ScreenType
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.module.ModuleDisableable
import com.github.sorusclient.client.setting.ConfigurableData
import com.github.sorusclient.client.setting.ConfigurableData.*
import com.github.sorusclient.client.setting.Setting

class Zoom : ModuleDisableable("zoom") {

    private var key: Setting<Key>
    private var fov: Setting<Double>
    private var sensitivity: Setting<Double>
    private var cinematicCamera: Setting<Boolean>
    private var toggled = false

    init {
        register("key", Setting(Key.C).also { key = it })
        register("fov", Setting(30.0).also { fov = it })
        register("sensitivity", Setting(0.5).also { sensitivity = it })
        register("cinematicCamera", Setting(false).also { cinematicCamera = it })
        EventManager.register(
            KeyEvent::class.java
        ) { event: KeyEvent -> onKey(event) }
    }

    private fun onKey(event: KeyEvent) {
        if (AdapterManager.getAdapter().openScreen == ScreenType.IN_GAME) {
            if (event.key == key.value && !event.isRepeat) {
                toggled = event.isPressed
            }
        }
    }

    fun applyZoom(): Boolean {
        return isEnabled() && toggled
    }

    fun getFovValue(): Double {
        return fov.value
    }

    fun getSensitivityValue(): Double {
        return sensitivity.value
    }

    fun useCinematicCamera(): Boolean {
        return cinematicCamera.value
    }

    override fun addSettings(settings: MutableList<ConfigurableData>) {
        super.addSettings(settings)
        settings.add(KeyBind("Key", key))
        settings.add(Slider("Field Of View", fov, 15.0, 100.0))
        settings.add(Slider("Sensitivity", sensitivity, 0.25, 1.5))
        settings.add(Toggle("Cinematic Camera", cinematicCamera))
    }

}