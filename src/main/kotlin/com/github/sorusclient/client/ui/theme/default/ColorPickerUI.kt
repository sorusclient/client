/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.ui.theme.default

import com.github.sorusclient.client.ui.*
import com.github.sorusclient.client.ui.framework.Container
import com.github.sorusclient.client.ui.framework.ContainerRenderer
import com.github.sorusclient.client.ui.framework.List
import com.github.sorusclient.client.ui.framework.constraint.Copy
import com.github.sorusclient.client.ui.framework.constraint.Relative
import com.github.sorusclient.client.ui.framework.constraint.Side
import com.github.sorusclient.client.util.Color

class ColorPickerUI(private val defaultTheme: DefaultTheme): Container() {

    init {
        storedState += "editedColor"
        storedState += "value"

        onStateUpdate["value"] = { state ->
            val stateValue = state["value"] as FloatArray
            val rgb = java.awt.Color.HSBtoRGB(stateValue[0], stateValue[1], stateValue[2])
            val javaColor = java.awt.Color(rgb)

            val color = state["editedColor"] as Color
            color.red = javaColor.red / 255.0
            color.green = javaColor.green / 255.0
            color.blue = javaColor.blue / 255.0
            color.alpha = stateValue[3].toDouble()
        }

        onInit += { state ->
            val color = state.second["editedColor"] as Color
            val javaColor = java.awt.Color(
                (color.red * 255).toInt(),
                (color.green * 255).toInt(),
                (color.blue * 255).toInt(),
                (color.alpha * 255).toInt()
            )
            val colorData = FloatArray(4)
            java.awt.Color.RGBtoHSB(javaColor.red, javaColor.green, javaColor.blue, colorData)
            colorData[3] = color.alpha.toFloat()
            state.second["value"] = colorData
        }

        children += Container()
            .apply {
                width = 0.2.toRelative()
                height = 0.5.toCopy()

                children += List(List.HORIZONTAL)
                    .apply {
                        x = Side.POSITIVE.toSide()
                        width = Relative(1.925, true)

                        paddingLeft = Relative(0.2, true)

                        backgroundColor = { defaultTheme.midgroundColor.value }.toDependent()
                        borderThickness = 0.4.toAbsolute()
                        borderColor = { defaultTheme.borderColor.value }.toDependent()
                        backgroundCornerRadius = 0.015.toRelative()

                        addChild(Container()
                            .apply {
                                width = Copy()
                                height = 0.85.toRelative()
                                setPadding(Relative(0.1, true))

                                topLeftBackgroundColor = Color.WHITE.toAbsolute()
                                bottomLeftBackgroundColor = Color.BLACK.toAbsolute()
                                bottomRightBackgroundColor = Color.BLACK.toAbsolute()
                                topRightBackgroundColor = { state: Map<String, Any> ->
                                    val colorData = state["value"] as FloatArray?
                                    val rgb = java.awt.Color.HSBtoRGB(colorData!![0], 1f, 1f)
                                    val javaColor = java.awt.Color(rgb)
                                    Color.fromRGB(javaColor.red, javaColor.green, javaColor.blue, 255)
                                }.toDependent()

                                onDrag = { state ->
                                    val colorData = state.first["value"] as FloatArray?
                                    val colorDataNew = floatArrayOf(
                                        colorData!![0],
                                        state.second.first.toFloat(),
                                        1 - state.second.second.toFloat(),
                                        colorData[3]
                                    )
                                    state.first["value"] = colorDataNew
                                }

                                children += Container()
                                    .apply {
                                        x = { state: Map<String, Any> ->
                                            val colorData = state["value"] as FloatArray
                                            Relative(colorData[1] - 0.5)
                                        }.toDependent()

                                        y = { state: Map<String, Any> ->
                                            val colorData = state["value"] as FloatArray
                                            Relative(1 - colorData[2] - 0.5)
                                        }.toDependent()

                                        width = 1.5.toAbsolute()
                                        height = Copy()

                                        backgroundColor = Color.WHITE.toAbsolute()
                                        backgroundCornerRadius = 0.75.toAbsolute()
                                    }
                            })

                        addChild(Container()
                            .apply {
                                width = 0.25.toCopy()
                                height = 0.85.toRelative()
                                setPadding(Relative(0.1, true))

                                backgroundImage = "assets/minecraft/color_range.png".toAbsolute()

                                onDrag = { state ->
                                    val colorData = state.first["value"] as FloatArray
                                    val colorDataNew = floatArrayOf(
                                        state.second.second.toFloat(),
                                        colorData[1],
                                        colorData[2],
                                        colorData[3]
                                    )
                                    state.first["value"] = colorDataNew
                                }

                                children += Container()
                                    .apply {
                                        x = 0.0.toRelative()
                                        y = { state: Map<String, Any> ->
                                            Relative((state["value"] as FloatArray)[0].toDouble() - 0.5)
                                        }.toDependent()

                                        width = 1.0.toRelative()
                                        height = 0.5.toAbsolute()

                                        backgroundColor = Color.WHITE.toAbsolute()
                                    }
                            })

                        addChild(Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 0.25.toCopy()
                                height = 0.85.toRelative()
                                setPadding(Relative(0.1, true))

                                topLeftBackgroundColor = Color.WHITE.toAbsolute()
                                topRightBackgroundColor = Color.WHITE.toAbsolute()
                                bottomLeftBackgroundColor = Color.fromRGB(255, 255, 255, 50).toAbsolute()
                                bottomRightBackgroundColor = Color.fromRGB(255, 255, 255, 50).toAbsolute()

                                onDrag = { state ->
                                    val colorData = state.first["value"] as FloatArray
                                    val colorDataNew = floatArrayOf(
                                        colorData[0],
                                        colorData[1],
                                        colorData[2],
                                        1 - state.second.second.toFloat()
                                    )
                                    state.first["value"] = colorDataNew
                                }

                                children += Container()
                                    .apply {
                                        x = 0.0.toRelative()
                                        y = { state: Map<String, Any> ->
                                            Relative((1 - (state["value"] as FloatArray)[3]).toDouble() - 0.5)
                                        }.toDependent()

                                        width = 1.0.toRelative()
                                        height = 0.5.toAbsolute()

                                        backgroundColor = Color.WHITE.toAbsolute()
                                    }
                            })

                        children += Container()
                            .apply {
                                y = Side.NEGATIVE.toSide()
                                width = 1.0.toCopy()
                                height = 0.2.toRelative()
                                setPadding(Relative(0.075, true))

                                borderThickness = 0.4.toAbsolute()
                                backgroundCornerRadius = 0.025.toRelative()
                                backgroundColor = { defaultTheme.backgroundColor.value }.toDependent()
                                borderColor = { state: Map<String, Any> ->
                                    if (state["hovered"] as Boolean) {
                                        defaultTheme.selectedBorderColor.value
                                    } else {
                                        defaultTheme.borderColor.value
                                    }
                                }.toDependent()

                                onClick = {
                                    ContainerRenderer.close(this)
                                }

                                children += Container()
                                    .apply {
                                        width = 0.5.toRelative()
                                        height = 1.0.toCopy()

                                        backgroundImage = "sorus/ui/settings/x.png".toAbsolute()
                                        backgroundColor = { defaultTheme.elementColor.value }.toDependent()
                                    }
                            }
                    }
            }
    }

}