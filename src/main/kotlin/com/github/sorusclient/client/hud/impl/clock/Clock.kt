/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.clock

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.hud.HUDElement
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedSetting.*
import com.github.sorusclient.client.util.Color
import java.time.LocalDateTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Clock: HUDElement("clock") {

    private val showSecondsHand: Setting<Boolean>

    private val secondsHandColor: Setting<Color>
    private val minutesHandColor: Setting<Color>
    private val hoursHandColor: Setting<Color>

    init {
        category.apply {
            data["showSecondsHand"] = SettingData(Setting(true).also { showSecondsHand = it })
            data["secondsHandColor"] = SettingData(Setting(Color.fromRGB(255, 50, 50, 255)).also { secondsHandColor = it })
            data["minutesHandColor"] = SettingData(Setting(Color.WHITE).also { minutesHandColor = it })
            data["hoursHandColor"] = SettingData(Setting(Color.WHITE).also { hoursHandColor = it })
        }

        uiCategory
            .apply {
                add(Toggle(showSecondsHand, "Show Seconds Hand"))
                add(Dependent(ColorPicker(secondsHandColor, "Seconds Hand Color"), showSecondsHand, true))
                add(ColorPicker(minutesHandColor, "Minutes Hand Color"))
                add(ColorPicker(hoursHandColor, "Hours Hand Color"))
            }
    }

    override fun render(x: Double, y: Double, scale: Double) {
        val renderer = AdapterManager.adapter.renderer
        renderer.drawRectangle(x, y, 50.0 * scale, 50.0 * scale, 25.0 * scale, Color.fromRGB(0, 0, 0, 50))

        val time = LocalDateTime.now()

        val centerX = x + 25.0 * scale
        val centerY = y + 25.0 * scale

        for (i in 0 until 12) {
            renderer.drawLine(centerX + cos((i / 12.0) * 2 * PI - PI / 2) * 22.5, centerY + sin((i / 12.0) * 2 * PI - PI / 2) * 22.5, centerX + cos((i / 12.0) * 2 * PI - PI / 2) * 18.75, centerY + sin((i / 12.0) * 2 * PI - PI / 2) * 18.75, 0.75, Color.WHITE)
        }

        renderer.drawLine(centerX, centerY, centerX + 20.0 * scale * cos(time.minute / 60.0 * 2 * PI - PI / 2), centerY + 20.0 * scale * sin(time.minute / 60.0 * 2 * PI - PI / 2), 1.25, minutesHandColor.value)
        renderer.drawLine(centerX, centerY, centerX + 12.5 * scale * cos((time.hour + time.minute / 60.0) / 12.0 * 2 * PI - PI / 2), centerY + 12.5 * scale * sin((time.hour + time.minute / 60.0) / 12.0 * 2 * PI - PI / 2), 1.625, hoursHandColor.value)

        if (showSecondsHand.value) {
            renderer.drawLine(centerX, centerY, centerX + 20.0 * scale * cos(time.second / 60.0 * 2 * PI - PI / 2), centerY + 20.0 * scale * sin(time.second / 60.0 * 2 * PI - PI / 2), 0.75, secondsHandColor.value)
        }

        renderer.drawRectangle(centerX - 1.25 * scale, centerY - 1.25 * scale, 2.5 * scale, 2.5 * scale, 1.25 * scale, Color.WHITE)
    }

    override val width: Double
        get() = 50.0
    override val height: Double
        get() = 50.0

    override val displayName: String
        get() = "Clock"

}