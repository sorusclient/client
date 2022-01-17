package com.github.sorusclient.client.module.impl.perspective

import com.github.glassmc.loader.GlassLoader
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.PerspectiveMode
import com.github.sorusclient.client.adapter.ScreenType
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.module.ModuleDisableable
import com.github.sorusclient.client.setting.ConfigurableData
import com.github.sorusclient.client.setting.ConfigurableData.KeyBind
import com.github.sorusclient.client.setting.Setting

class Perspective : ModuleDisableable("perspective") {

    private var key: Setting<Key>
    var isToggled = false
        private set
    private var previousPerspective: PerspectiveMode = PerspectiveMode.FIRST_PERSON

    init {
        register("key", Setting(Key.F).also { key = it })
        EventManager.register(this::onKey)
    }

    private fun onKey(event: KeyEvent) {
        val adapter = AdapterManager.getAdapter()
        if (isEnabled() && adapter.openScreen == ScreenType.IN_GAME) {
            if (event.key == key.value && !event.isRepeat) {
                isToggled = event.isPressed
                if (isToggled) {
                    previousPerspective = adapter.perspective
                    GlassLoader.getInstance().getInterface(IPerspectiveHelper::class.java).onToggle()
                    adapter.perspective = PerspectiveMode.THIRD_PERSON_BACK
                } else {
                    adapter.perspective = previousPerspective
                }
            }
        }
    }

    override fun addSettings(settings: MutableList<ConfigurableData>) {
        super.addSettings(settings)
        settings.add(KeyBind("Key", key))
    }

}