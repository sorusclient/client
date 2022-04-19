/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.zoom

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.ScreenType
import com.github.sorusclient.client.adapter.event.GetFOVEvent
import com.github.sorusclient.client.adapter.event.GetSensitivityEvent
import com.github.sorusclient.client.adapter.event.GetUseCinematicCamera
import com.github.sorusclient.client.adapter.event.TickEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting
import com.github.sorusclient.client.util.keybind.KeyBind
import com.github.sorusclient.client.util.keybind.KeyBindManager

object Zoom {

    private val enabled: Setting<Boolean>
    private val key: Setting<out MutableList<Key>>
    private val fovDivisor: Setting<Double>
    private val sensitivity: Setting<Double>
    private val cinematicCamera: Setting<Boolean>
    private val animation: Setting<Boolean>

    private var toggled = false

    private var lastAnimatedFov = 1.0
    private var animatedFov = 1.0
    private var animationUpdateTime = -1L
    private var animationTarget = 1.0

    init {
        SettingManager.settingsCategory
            .apply {
                data["zoom"] = CategoryData()
                        .apply {
                            data["enabled"] = SettingData(Setting(false).also { enabled = it })
                            data["key"] = SettingData(Setting(arrayListOf(Key.C)).also { key = it })
                            data["fovDivisor"] = SettingData(Setting(4.0).also { fovDivisor = it })
                            data["sensitivity"] = SettingData(Setting(0.5).also { sensitivity = it })
                            data["cinematicCamera"] = SettingData(Setting(false).also { cinematicCamera = it })
                            data["animation"] = SettingData(Setting(true).also { animation = it })
                        }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Zoom")
                    .apply {
                        add(DisplayedSetting.Toggle(enabled, "Enabled"))
                        add(DisplayedSetting.KeyBind(key, "Key"))
                        add(DisplayedSetting.Slider(fovDivisor, "FOV Divisor", 2.0, 64.0))
                        add(DisplayedSetting.Slider(sensitivity, "Sensitivity", 0.25, 1.0))
                        add(DisplayedSetting.Toggle(cinematicCamera, "Cinematic Camera"))
                        add(DisplayedSetting.Toggle(animation, "Animation"))
                    })
            }

        EventManager.register<GetFOVEvent> { event ->
            if (applyAnimation()) {
                animationTarget = 1.0

                if (toggled) {
                    animationTarget /= fovDivisor.value;
                }

                val multiplier = Math.min(lastAnimatedFov +
                        (animatedFov - lastAnimatedFov) * ((System.currentTimeMillis() - animationUpdateTime) / 50.0), 1.0)

                event.fov *= multiplier
            } else if (applyZoom()) {
                event.fov /= fovDivisor.value
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

        EventManager.register<TickEvent> {
            animationUpdateTime = System.currentTimeMillis()
            lastAnimatedFov = animatedFov
            animatedFov += (animationTarget - animatedFov) * 0.75F
        }

        KeyBindManager.register(KeyBind(
            {
                key.value
            },
            this::onKeyUpdate
        ))
    }

    private fun onKeyUpdate(pressed: Boolean) {
        if (AdapterManager.adapter.openScreen == ScreenType.IN_GAME) {
            toggled = pressed
        }
    }

    private fun applyZoom(): Boolean {
        return enabled.value && toggled
    }

    private fun applyAnimation(): Boolean {
        return enabled.value && animation.value
    }

}