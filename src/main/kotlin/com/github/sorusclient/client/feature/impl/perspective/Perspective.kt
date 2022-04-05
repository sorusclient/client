/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.perspective

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.PerspectiveMode
import com.github.sorusclient.client.adapter.ScreenType
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedSetting
import com.github.sorusclient.client.util.keybind.KeyBind
import com.github.sorusclient.client.util.keybind.KeyBindManager

object Perspective {

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
                        data["key"] = SettingData(Setting(arrayListOf(Key.X)).also { key = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Perspective"))
                    .apply {
                        add(DisplayedSetting.Toggle(enabled, "Enabled"))
                        add(DisplayedSetting.KeyBind(key, "Key"))
                    }
            }

        KeyBindManager.register(KeyBind(
                {
                    key.value
                },
                this::onKeyUpdate,
            )
        )
    }

    fun isEnabled(): Boolean {
        return enabled.value
    }

    private fun onKeyUpdate(pressed: Boolean) {
        val adapter = AdapterManager.adapter
        if (isEnabled() && adapter.openScreen == ScreenType.IN_GAME) {

            if (pressed) {
                previousPerspective = adapter.perspective
                InterfaceManager.get<IPerspectiveHelper>().onToggle()
                adapter.perspective = PerspectiveMode.THIRD_PERSON_BACK
            } else {
                adapter.perspective = previousPerspective
            }

            isToggled = pressed
        }
    }

}