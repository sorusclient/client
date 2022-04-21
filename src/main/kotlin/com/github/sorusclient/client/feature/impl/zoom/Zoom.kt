/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.zoom

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.ScreenType
import com.github.sorusclient.client.adapter.event.*
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting
import com.github.sorusclient.client.util.keybind.KeyBind
import com.github.sorusclient.client.util.keybind.KeyBindManager
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

object Zoom {

    private val zoomHelper: IZoomHelper = InterfaceManager.get()

    private val enabled: Setting<Boolean>
    private val key: Setting<out MutableList<Key>>
    private val fovMultiplier: Setting<Double>
    private val sensitivity: Setting<Double>
    private val cinematicCamera: Setting<Boolean>
    private val animation: Setting<Boolean>
    private val scrollZoom: Setting<Boolean>

    private var toggled = false

    private var lastAnimatedFov = 1.0
    private var animatedFov = 1.0
    private var animationUpdateTime = -1L
    private var animationTarget = 1.0

    private var scroll = 0.0

    init {
        SettingManager.settingsCategory
            .apply {
                data["zoom"] = CategoryData()
                        .apply {
                            data["enabled"] = SettingData(Setting(false).also { enabled = it })
                            data["key"] = SettingData(Setting(arrayListOf(Key.C)).also { key = it })
                            data["fovMultiplier"] = SettingData(Setting(0.33).also { fovMultiplier = it })
                            data["sensitivity"] = SettingData(Setting(0.5).also { sensitivity = it })
                            data["cinematicCamera"] = SettingData(Setting(false).also { cinematicCamera = it })
                            data["animation"] = SettingData(Setting(false).also { animation = it })
                            data["scrollZoom"] = SettingData(Setting(false).also { scrollZoom = it })
                        }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Zoom")
                    .apply {
                        add(DisplayedSetting.Toggle(enabled, "Enabled"))
                        add(DisplayedSetting.KeyBind(key, "Key"))
                        add(DisplayedSetting.Slider(fovMultiplier, "FOV Multiplier", 0.1, 0.75))
                        add(DisplayedSetting.Slider(sensitivity, "Sensitivity", 0.25, 1.0))
                        add(DisplayedSetting.Toggle(cinematicCamera, "Cinematic Camera"))
                        add(DisplayedSetting.Toggle(animation, "Animation"))
                        add(DisplayedSetting.Toggle(scrollZoom, "Scroll Adjusted Zoom"))
                    })
            }

        var cachedFov = 0.0
        EventManager.register<GetFOVEvent> { event ->
            if (applyAnimation()) {
                val multiplier = max(0.1, min(lastAnimatedFov + (animatedFov - lastAnimatedFov) * ((System.currentTimeMillis() - animationUpdateTime) / 50.0), 1.0))
                event.fov *= multiplier
            } else if (applyZoom()) {
                event.fov *= fovMultiplier.value
            }

            if (cachedFov != event.fov) {
                zoomHelper.onUpdateZoom()
            }
            cachedFov = event.fov
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

            scroll /= 2
        }

        EventManager.register<MouseEvent> {
            if (it.wheel != 0.0 && scrollZoom.value) {
                scroll += if (scroll + it.wheel == 0.0) { 0.0 } else { (scroll + it.wheel).absoluteValue.pow(1.3) * if (it.wheel > 0) { 1 } else { -1 } }

                scroll = scroll.coerceAtMost(10.0).coerceAtLeast(-10.0)

                if (applyAnimation() && applyZoom()) {
                    animationTarget -= scroll * 0.01
                    animatedFov -= scroll * 0.01

                    animationTarget = max(0.1, min(fovMultiplier.value, animationTarget))
                    animatedFov = max(0.1, min(fovMultiplier.value, animatedFov))
                }
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
        if (AdapterManager.adapter.openScreen == ScreenType.IN_GAME) {
            toggled = pressed

            if (toggled && animation.value) {
                animationTarget = fovMultiplier.value
                animatedFov = 1.0
                lastAnimatedFov = animatedFov
            } else if (!toggled && animation.value) {
                animationTarget = 1.0
            }
        }
    }

    private fun applyZoom(): Boolean {
        return enabled.value && toggled
    }

    private fun applyAnimation(): Boolean {
        return enabled.value && animation.value
    }

    fun isScrollZoom(): Boolean {
        return applyZoom() && scrollZoom.value
    }

}