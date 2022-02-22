package com.github.sorusclient.client.feature.impl.perspective

import com.github.glassmc.loader.api.GlassLoader
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.PerspectiveMode
import com.github.sorusclient.client.adapter.ScreenType
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData

class Perspective {

    private val enabled: Setting<Boolean>
    private val key: Setting<out MutableList<Key>>

    var isToggled = false
        private set
    private var previousPerspective: PerspectiveMode = PerspectiveMode.FIRST_PERSON

    init {
        SettingManager.settingsCategory
            .apply {
                data["perspective"] = CategoryData()
                    .apply {
                        data["enabled"] = SettingData(Setting(false).also { enabled = it })
                        data["key"] = SettingData(Setting(arrayListOf(Key.F)).also { key = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Perspective"))
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
            if (event.key == key.value[0] && !event.isRepeat) {
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