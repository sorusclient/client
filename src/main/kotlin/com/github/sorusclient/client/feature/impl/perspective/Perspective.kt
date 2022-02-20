package com.github.sorusclient.client.feature.impl.perspective

import com.github.glassmc.loader.api.GlassLoader
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.PerspectiveMode
import com.github.sorusclient.client.adapter.ScreenType
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.Category
import com.github.sorusclient.client.setting.SettingConfigure.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager

class Perspective {

    private val enabled: Setting<Boolean>
    private val key: Setting<Key>

    var isToggled = false
        private set
    private var previousPerspective: PerspectiveMode = PerspectiveMode.FIRST_PERSON

    init {
        SettingManager.settingsCategory
            .apply {
                put("perspective", HashMap<String, Any>()
                    .apply {
                        put("enabled", Setting(false).also { enabled = it })
                        put("key", Setting(Key.F).also { key = it })
                    })
            }

        SettingManager.mainUICategory
            .apply {
                add(Category("Perspective"))
                    .apply {
                        add(Toggle(enabled, "Enabled"))
                        add(KeyBind(key, "Key"))
                    }
            }

        EventManager.register(this::onKey)
    }

    fun isEnabled(): Boolean {
        return enabled.value
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

}