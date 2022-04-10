/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.ui.theme.default

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.event.call
import com.github.sorusclient.client.notification.Interaction
import com.github.sorusclient.client.notification.Notification
import com.github.sorusclient.client.notification.NotificationManager
import com.github.sorusclient.client.notification.close
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting
import com.github.sorusclient.client.ui.*
import com.github.sorusclient.client.ui.framework.*
import com.github.sorusclient.client.ui.framework.constraint.*
import com.github.sorusclient.client.ui.theme.DefaultThemeInitializeEvent
import com.github.sorusclient.client.ui.theme.Theme
import com.github.sorusclient.client.util.Color
import java.lang.reflect.InvocationTargetException
import kotlin.math.max

class DefaultTheme: Theme() {

    internal val backgroundColor: Setting<Color>
    internal val borderColor: Setting<Color>
    internal val midgroundColor: Setting<Color>
    internal val selectedColor: Setting<Color>
    internal val selectedBorderColor: Setting<Color>
    internal val elementColor: Setting<Color>

    private lateinit var mainGui: Container
    private lateinit var searchGui: Container
    private lateinit var colorPickerGui: Container
    private lateinit var notificationsUi: Container

    override val items: MutableMap<String, Container> = mutableMapOf()

    init {
        EventManager.register { event: KeyEvent ->
            if (event.isPressed && event.key == Key.U) {
                initialize()
            }
        }

        category
            .apply {
                Color.fromRGB(15, 15, 15, 200)
                data["backgroundColor"] = SettingData(Setting(Color.fromRGB(8, 8, 8, 200)).also { backgroundColor = it })
                data["borderColor"] = SettingData(Setting(Color.fromRGB(10, 10, 10, 200)).also { borderColor = it })
                data["midgroundColor"] = SettingData(Setting(Color.fromRGB(0, 0, 0, 65)).also { midgroundColor = it })
                data["selectedColor"] = SettingData(Setting(Color.fromRGB(60, 75, 250, 65)).also { selectedColor = it })
                data["selectedBorderColor"] = SettingData(Setting(Color.fromRGB(60, 75, 250, 255)).also { selectedBorderColor = it })
                data["elementColor"] = SettingData(Setting(Color.WHITE).also { elementColor = it })
            }

        uiCategory
            .apply {
                add(DisplayedSetting.ColorPicker(backgroundColor, "Background Color"))
                add(DisplayedSetting.ColorPicker(borderColor, "Border Color"))
                add(DisplayedSetting.ColorPicker(midgroundColor, "Midground Color"))
                add(DisplayedSetting.ColorPicker(selectedColor, "Selected Color"))
                add(DisplayedSetting.ColorPicker(selectedBorderColor, "Selected Border Color"))
                add(DisplayedSetting.ColorPicker(elementColor, "Element Color"))
            }
    }

    internal fun getSetting(setting: DisplayedSetting): Container {
        when (setting) {
            is DisplayedSetting.Toggle -> {
                return Container()
                    .apply {
                        height = 15.0.toAbsolute()

                        onUpdate += { state ->
                            state["hidden"] = false
                        }

                        children += Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 0.75.toRelative()

                                children += Container()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.0666.toRelative()
                                    }

                                children += Text()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                        text = setting.displayName.toAbsolute()
                                        scale = 0.0033.toRelative()
                                        textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                    }

                                children += Container()
                                    .apply {
                                        x = Side.POSITIVE.toSide()
                                        width = 2.0.toCopy()
                                        height = 0.6.toRelative()
                                        setPadding(Relative(0.2, true))

                                        backgroundColor = Dependent {
                                            val toggled = setting.setting.value
                                            if (toggled) {
                                                { this@DefaultTheme.selectedColor.value }.toDependent()
                                            } else {
                                                { this@DefaultTheme.midgroundColor.value }.toDependent()
                                            }
                                        }
                                        borderThickness = 0.4.toAbsolute()
                                        borderColor = Dependent { state ->
                                            val toggled = setting.setting.value
                                            if ((state["hovered"] as Boolean && !setting.setting.isForcedValue) || toggled) {
                                                { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                            } else {
                                                { this@DefaultTheme.borderColor.value }.toDependent()
                                            }
                                        }
                                        backgroundCornerRadius = 0.01.toRelative()

                                        onClick = {
                                            if (!setting.setting.isForcedValue) {
                                                setting.setting.setValueRaw(!setting.setting.realValue)

                                                setting.setting.overriden = true
                                            }
                                        }

                                        children += Container()
                                            .apply {
                                                x = { _: Map<String, Any> ->
                                                    Side(if (setting.setting.value) 1 else -1)
                                                }.toDependent()
                                                width = Copy()
                                                height = 0.7.toRelative()
                                                setPadding(Relative(0.15, true))

                                                backgroundCornerRadius = 0.075.toRelative()
                                                backgroundColor = Color.WHITE.toAbsolute()
                                            }
                                    }
                            }
                    }
            }
            is DisplayedSetting.Slider -> {
                return Container()
                    .apply {
                        height = 15.0.toAbsolute()
                        onUpdate += { state ->
                            state["hidden"] = false
                        }

                        children += Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 0.75.toRelative()

                                children += Container()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.0666.toRelative()
                                    }

                                children += Text()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                        text = setting.displayName.toAbsolute()
                                        scale = 0.0033.toRelative()
                                        textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                    }

                                children += Container()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.0666.toRelative()
                                    }

                                children += Container()
                                    .apply {
                                        x = Side.POSITIVE.toSide()
                                        width = 5.0.toCopy()
                                        height = 0.6.toRelative()
                                        setPadding(Relative(0.2, true))

                                        backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()

                                        borderThickness = 0.4.toAbsolute()
                                        borderColor = Dependent { state ->
                                            if (state["hovered"] as Boolean || state["interacted"] as Boolean) {
                                                { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                            } else {
                                                { this@DefaultTheme.borderColor.value }.toDependent()
                                            }
                                        }
                                        backgroundCornerRadius = 0.01.toRelative()

                                        onDrag = { state ->
                                            if (!setting.setting.isForcedValue) {
                                                val minimum = setting.minimum
                                                val maximum = setting.maximum
                                                val value = state.second.first
                                                state.first["value"] = value
                                                val actualSetting: Setting<*> = setting.setting
                                                val valueToSet = (maximum - minimum) * value + minimum
                                                when (actualSetting.type) {
                                                    java.lang.Double::class.java -> {
                                                        actualSetting.setValueRaw(valueToSet)
                                                    }
                                                    java.lang.Long::class.java -> {
                                                        actualSetting.setValueRaw(valueToSet.toLong())
                                                    }
                                                    else -> {
                                                        error("Strange Type on Slider")
                                                    }
                                                }

                                                setting.setting.overriden = true
                                            }
                                        }

                                        children += Container()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = { _: Map<String, Any> ->
                                                    val minimum = setting.minimum
                                                    val maximum = setting.maximum
                                                    Flexible((setting.setting.value.toDouble() - minimum) / (maximum - minimum))
                                                }.toDependent()
                                                setPadding(Relative(0.1, true))

                                                backgroundColor = Color.WHITE.toAbsolute()
                                                backgroundCornerRadius = Relative(0.2, true)
                                            }

                                        children += Container()
                                            .apply {
                                                x = { _: Map<String, Any> ->
                                                    val minimum = setting.minimum
                                                    val maximum = setting.maximum
                                                    Relative((setting.setting.value.toDouble() - minimum) / (maximum - minimum) - 0.5)
                                                }.toDependent()
                                                y = 0.0.toRelative()
                                                width = Copy()
                                                height = 1.0.toRelative()

                                                backgroundCornerRadius = Relative(0.25, true)
                                            }
                                    }

                                storedState += "value"
                                runtime.setState(
                                    "value",
                                    (setting.setting.value.toDouble() - setting.minimum) / (setting.maximum - setting.minimum)
                                )
                            }
                    }
            }
            is DisplayedSetting.KeyBind -> {
                return Container()
                    .apply {
                        height = 15.0.toAbsolute()
                        onUpdate += { state ->
                            state["hidden"] = false
                        }

                        children += Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 0.75.toRelative()

                                children += Container()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.0666.toRelative()
                                    }

                                children += Text()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                        text = setting.displayName.toAbsolute()
                                        scale = 0.0033.toRelative()
                                        textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                    }

                                children += Container()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.0666.toRelative()
                                    }

                                children += Container()
                                    .apply {
                                        x = Side.POSITIVE.toSide()
                                        width = 5.0.toCopy()
                                        height = 0.6.toRelative()
                                        setPadding(Relative(0.2, true))

                                        backgroundColor = Dependent { state ->
                                            if (state["clicked"] != null && state["clicked"] as Boolean) {
                                                { this@DefaultTheme.selectedColor.value }.toDependent()
                                            } else {
                                                { this@DefaultTheme.midgroundColor.value }.toDependent()
                                            }
                                        }
                                        borderThickness = 0.4.toAbsolute()
                                        borderColor = Dependent { state ->
                                            if ((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) {
                                                { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                            } else {
                                                { this@DefaultTheme.borderColor.value }.toDependent()
                                            }
                                        }
                                        backgroundCornerRadius = 0.01.toRelative()

                                        val pressedKeys = ArrayList<Key>()

                                        onKey = onKey@{ state ->
                                            if (state.second.key == Key.UNKNOWN) return@onKey
                                            if (state.second.isPressed) {
                                                pressedKeys += state.second.key

                                                setting.setting.setValueRaw(ArrayList(pressedKeys))
                                                setting.setting.overriden = true
                                            } else {
                                                state.first["selected2"] = false
                                                pressedKeys.clear()
                                            }
                                        }

                                        children += Text()
                                            .apply {
                                                fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                                text = { state: Map<String, Any> ->
                                                    if (pressedKeys.isEmpty() && state["selected2"] as Boolean) {
                                                        "..."
                                                    } else {
                                                        setting.setting.value.joinToString(" + ")
                                                    }
                                                }.toDependent()
                                                scale = 0.01.toRelative()
                                                textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                            }
                                    }

                                onStateUpdate["selected"] = { state ->
                                    if (state["selected"] != null) {
                                        state["selected2"] = state["selected"] as Any
                                    }
                                }

                                storedState += "selected2"

                                onInit += { state ->
                                    state.second["selected2"] = false
                                }
                            }
                    }
            }
            is DisplayedSetting.ClickThrough -> {
                return Container()
                    .apply {
                        height = 15.0.toAbsolute()
                        onUpdate += { state ->
                            state["hidden"] = false
                        }

                        children += Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 0.75.toRelative()

                                children += Container()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.0666.toRelative()
                                    }

                                children += Text()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                        text = setting.displayName.toAbsolute()
                                        scale = 0.0033.toRelative()
                                        textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                    }

                                children += Container()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.0666.toRelative()
                                    }

                                children += Container()
                                    .apply {
                                        x = Side.POSITIVE.toSide()
                                        width = 5.0.toCopy()
                                        height = 0.6.toRelative()
                                        setPadding(Relative(0.2, true))

                                        backgroundColor = Dependent { state ->
                                            if (state["clicked"] != null && state["clicked"] as Boolean) {
                                                { this@DefaultTheme.selectedColor.value }.toDependent()
                                            } else {
                                                { this@DefaultTheme.midgroundColor.value }.toDependent()
                                            }
                                        }
                                        borderThickness = 0.4.toAbsolute()
                                        borderColor = Dependent { state ->
                                            if ((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) {
                                                { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                            } else {
                                                { this@DefaultTheme.borderColor.value }.toDependent()
                                            }
                                        }
                                        backgroundCornerRadius = 0.01.toRelative()

                                        children += Container()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = Copy()
                                                height = 0.6.toRelative()
                                                setPadding(Relative(0.15, true))

                                                backgroundImage = "assets/minecraft/arrow_left.png".toAbsolute()

                                                onClick = { state ->
                                                    var newValue = state["clickThroughValue"] as Int - 1
                                                    if (newValue < 0) {
                                                        newValue = (setting.setting.type.getDeclaredMethod("values").invoke(null) as Array<*>).size - 1
                                                    }
                                                    state["clickThroughValue"] = newValue

                                                    setting.setting.overriden = true
                                                }

                                                consumeClicks = false
                                            }

                                        children += Container()
                                            .apply {
                                                x = Side.POSITIVE.toSide()
                                                width = Copy()
                                                height = 0.6.toRelative()
                                                setPadding(Relative(0.15, true))

                                                backgroundImage = "assets/minecraft/arrow_right.png".toAbsolute()

                                                onClick = { state ->
                                                    var valuesLength = 0
                                                    try {
                                                        valuesLength = (setting.setting.type.getDeclaredMethod("values").invoke(null) as Array<*>).size
                                                    } catch (e: IllegalAccessException) {
                                                        e.printStackTrace()
                                                    } catch (e: InvocationTargetException) {
                                                        e.printStackTrace()
                                                    } catch (e: NoSuchMethodException) {
                                                        e.printStackTrace()
                                                    }
                                                    if (state["clickThroughValue"] as Int + 1 >= valuesLength) {
                                                        state["clickThroughValue"] = 0
                                                    } else {
                                                        state["clickThroughValue"] = state["clickThroughValue"] as Int + 1
                                                    }

                                                    setting.setting.overriden = true
                                                }

                                                consumeClicks = false
                                            }

                                        children += Text()
                                            .apply {
                                                fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                                text = { _: Map<String, Any> ->
                                                    setting.setting.value.toString()
                                                }.toDependent()
                                                scale = 0.01.toRelative()
                                                textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                            }

                                        onStateUpdate["clickThroughValue"] = { state ->
                                            val values = setting.setting.type.getDeclaredMethod("values").invoke(null) as Array<*>
                                            val setting1: Setting<*> = setting.setting
                                            if (setting1.isForcedValue) {
                                                var index = state["clickThroughValue"] as Int
                                                while (index != -1) {
                                                    if (setting1.forcedValues!!.contains(values[index])) {
                                                        setting1.setValueRaw(values[index]!!)
                                                        state["clickThroughValue"] = index
                                                        index = -1
                                                    } else {
                                                        index++
                                                        if (index >= values.size) {
                                                            index = 0
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (state["clickThroughValue"] != null) {
                                                    state["clickThroughValue"].let { setting1.setValueRaw(values[it as Int]!!) }
                                                }
                                            }
                                        }
                                    }

                                storedState += "clickThroughValue"
                                runtime.setState("clickThroughValue", setting.setting.value.ordinal)
                            }
                    }
            }
            is DisplayedSetting.ColorPicker -> {
                return Container()
                    .apply {
                        height = 15.0.toAbsolute()

                        children += Container()
                            .apply {
                                onUpdate += { state ->
                                    state["hidden"] = false
                                }

                                children += Container()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.066.toRelative()
                                    }

                                children += Text()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                        text = setting.displayName.toAbsolute()
                                        scale = 0.0033.toRelative()
                                        textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                    }

                                children += Container()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.066.toRelative()
                                    }

                                children += Container()
                                    .apply {
                                        x = Side.POSITIVE.toSide()
                                        width = Copy()
                                        height = 0.6.toRelative()
                                        setPadding(Relative(0.2, true))

                                        backgroundColor = { _: Map<String, Any> ->
                                            setting.setting.value
                                        }.toDependent()

                                        onClick = {
                                            val color = Color(setting.setting.realValue.red, setting.setting.realValue.green, setting.setting.realValue.blue, setting.setting.realValue.alpha)
                                            colorPickerGui
                                                .apply {
                                                    runtime.setState("editedColor", color)
                                                }

                                            setting.setting.setValueRaw(color)
                                            ContainerRenderer.open(colorPickerGui)
                                        }

                                        backgroundCornerRadius = 0.01.toRelative()
                                    }

                                x = Side.NEGATIVE.toSide()
                                width = 0.75.toRelative()
                            }
                    }
            }
            is DisplayedSetting.CustomTextColor -> {
                return Container()
                    .apply {
                        height = {
                            ((setting.setting.value.size * 0.22 + (setting.setting.value.size + 1) * 0.025) * 0.525).toCopy().getHeightValue(runtime) + 15.0
                        }.toDependent()

                        onUpdate += { state ->
                            state["hidden"] = false
                        }

                        children += Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 0.75.toRelative()

                                children += Container()
                                    .apply {
                                        y = Side.NEGATIVE.toSide()
                                        height = 15.0.toAbsolute()

                                        children += Container()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = 0.0666.toRelative()
                                            }

                                        children += Text()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()

                                                fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                                text = setting.displayName.toAbsolute()
                                                scale = 0.0033.toRelative()
                                                textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                            }

                                        children += Container()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = 0.0666.toRelative()
                                            }
                                    }

                                children += Container()
                                    .apply {
                                        y = Side.POSITIVE.toSide()
                                        width = 0.965.toRelative()

                                        backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()
                                        borderThickness = 0.4.toAbsolute()
                                        borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                        backgroundCornerRadius = 0.01.toRelative()

                                        storedState += "hasInitList"

                                        children += List(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = 0.75.toRelative()

                                                onStateUpdate["hasInitList"] = { state ->
                                                    if (!(state["hasInitList"] as Boolean)) {
                                                        state["hasInit"] = false
                                                        state["hasInitList"] = true
                                                    }
                                                }

                                                onInit += {
                                                    clear()

                                                    for (line in setting.setting.value) {
                                                        children += Container()
                                                            .apply {
                                                                height = 0.22.toCopy()
                                                                setPadding(0.025.toRelative())

                                                                backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                borderThickness = 0.4.toAbsolute()
                                                                borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                                backgroundCornerRadius = 0.02.toRelative()

                                                                children += Container()
                                                                    .apply {
                                                                        x = Side.POSITIVE.toSide()
                                                                        width = 1.0.toCopy()
                                                                        height = 0.5.toRelative()
                                                                        setPadding(0.0125.toRelative())

                                                                        backgroundColor = { state: Map<String, Any> ->
                                                                            if (state["clicked"] as Boolean) {
                                                                                this@DefaultTheme.selectedColor.value
                                                                            } else {
                                                                                this@DefaultTheme.midgroundColor.value
                                                                            }
                                                                        }.toDependent()
                                                                        borderThickness = 0.4.toAbsolute()
                                                                        borderColor = { state: Map<String, Any> ->
                                                                            if (state["hovered"] as Boolean || state["clicked"] as Boolean) {
                                                                                this@DefaultTheme.selectedBorderColor.value
                                                                            } else {
                                                                                this@DefaultTheme.borderColor.value
                                                                            }
                                                                        }.toDependent()
                                                                        backgroundCornerRadius = 0.02.toRelative()

                                                                        children += Container()
                                                                            .apply {
                                                                                width = 0.5.toRelative()
                                                                                height = 1.0.toCopy()

                                                                                backgroundImage = "sorus/ui/profiles/delete.png".toAbsolute()
                                                                                backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                            }

                                                                        onClick = { state ->
                                                                            setting.setting.value.remove(line)
                                                                            state["hasInitList"] = false
                                                                        }
                                                                    }

                                                                children += Container()
                                                                    .apply {
                                                                        x = Side.POSITIVE.toSide()
                                                                        width = 1.0.toCopy()
                                                                        height = 0.5.toRelative()
                                                                        setPadding(0.0125.toRelative())

                                                                        backgroundColor = { state: Map<String, Any> ->
                                                                            if (state["clicked"] as Boolean) {
                                                                                this@DefaultTheme.selectedColor.value
                                                                            } else {
                                                                                this@DefaultTheme.midgroundColor.value
                                                                            }
                                                                        }.toDependent()
                                                                        borderThickness = 0.4.toAbsolute()
                                                                        borderColor = { state: Map<String, Any> ->
                                                                            if (state["hovered"] as Boolean || state["clicked"] as Boolean) {
                                                                                this@DefaultTheme.selectedBorderColor.value
                                                                            } else {
                                                                                this@DefaultTheme.borderColor.value
                                                                            }
                                                                        }.toDependent()
                                                                        backgroundCornerRadius = 0.02.toRelative()

                                                                        children += Container()
                                                                            .apply {
                                                                                width = 0.5.toRelative()
                                                                                height = 1.0.toCopy()

                                                                                backgroundImage = "sorus/ui/themes/add.png".toAbsolute()
                                                                                backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                            }

                                                                        onClick = { state ->
                                                                            line.add(com.github.sorusclient.client.util.Pair("", Color.WHITE))
                                                                            state["hasInitList"] = false
                                                                        }
                                                                    }

                                                                children += List(com.github.sorusclient.client.ui.framework.List.HORIZONTAL)
                                                                    .apply {
                                                                        for (element in line) {
                                                                            children += Container()
                                                                                .apply {
                                                                                    width = {
                                                                                        max(35.0, AdapterManager.adapter.renderer.getTextWidth("sorus/ui/font/Quicksand-SemiBold.ttf", element.first)) * 0.6575 + Relative(0.15, true).getWidthValue(this.runtime) * 2
                                                                                    }.toDependent()
                                                                                    setPadding(0.0125.toRelative())

                                                                                    backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                                    borderThickness = 0.4.toAbsolute()
                                                                                    borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                                                    backgroundCornerRadius = 0.02.toRelative()

                                                                                    children += Text()
                                                                                        .apply {
                                                                                            x = Side.NEGATIVE.toSide()
                                                                                            y = Side.NEGATIVE.toSide()
                                                                                            fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                                            scale = Relative(0.025, true)
                                                                                            text = { element.first }.toDependent()

                                                                                            setPadding(Relative(0.15, true))
                                                                                        }

                                                                                    children += Container()
                                                                                        .apply {
                                                                                            x = Side.POSITIVE.toSide()
                                                                                            y = Side.POSITIVE.toSide()
                                                                                            width = 1.0.toCopy()
                                                                                            height = 0.4.toRelative()
                                                                                            setPadding(Relative(0.075, true))

                                                                                            backgroundColor = { state: Map<String, Any> ->
                                                                                                if (state["clicked"] as Boolean) {
                                                                                                    this@DefaultTheme.selectedColor.value
                                                                                                } else {
                                                                                                    this@DefaultTheme.midgroundColor.value
                                                                                                }
                                                                                            }.toDependent()
                                                                                            borderThickness = 0.4.toAbsolute()
                                                                                            borderColor = { state: Map<String, Any> ->
                                                                                                if (state["hovered"] as Boolean || state["clicked"] as Boolean) {
                                                                                                    this@DefaultTheme.selectedBorderColor.value
                                                                                                } else {
                                                                                                    this@DefaultTheme.borderColor.value
                                                                                                }
                                                                                            }.toDependent()
                                                                                            backgroundCornerRadius = 0.04.toRelative()

                                                                                            children += Container()
                                                                                                .apply {
                                                                                                    width = 0.5.toRelative()
                                                                                                    height = 1.0.toCopy()

                                                                                                    backgroundImage = "sorus/ui/profiles/delete.png".toAbsolute()
                                                                                                    backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                                }

                                                                                            onClick = { state ->
                                                                                                line.remove(element)
                                                                                                state["hasInitList"] = false
                                                                                            }
                                                                                        }

                                                                                    children += Container()
                                                                                        .apply {
                                                                                            x = Side.NEGATIVE.toSide()
                                                                                            y = Side.POSITIVE.toSide()
                                                                                            width = 1.0.toCopy()
                                                                                            height = 0.4.toRelative()
                                                                                            setPadding(Relative(0.075, true))

                                                                                            backgroundColor = { element.second }.toDependent()
                                                                                            backgroundCornerRadius = 0.04.toRelative()

                                                                                            onClick = {
                                                                                                colorPickerGui
                                                                                                    .apply {
                                                                                                        runtime.setState("editedColor", element.second)
                                                                                                    }

                                                                                                ContainerRenderer.open(colorPickerGui)
                                                                                            }
                                                                                        }

                                                                                    onKey = onKey@{ state ->
                                                                                        if (!state.second.isPressed) return@onKey

                                                                                        if (state.second.key == Key.BACKSPACE && element.first.isNotEmpty()) {
                                                                                            element.first = element.first.substring(0, element.first.length - 1)
                                                                                        }
                                                                                    }

                                                                                    onChar = { state ->
                                                                                        element.first += state.second.character
                                                                                    }
                                                                                }
                                                                        }
                                                                    }
                                                            }
                                                    }
                                                }
                                            }

                                        children += Container()
                                            .apply {
                                                x = Side.POSITIVE.toSide()
                                                y = Side.POSITIVE.toSide()
                                                width = 0.075.toRelative()
                                                height = 1.0.toCopy()

                                                setPadding(0.0125.toRelative())

                                                backgroundColor = { state: Map<String, Any> ->
                                                    if (state["clicked"] as Boolean) {
                                                        this@DefaultTheme.selectedColor.value
                                                    } else {
                                                        this@DefaultTheme.midgroundColor.value
                                                    }
                                                }.toDependent()
                                                borderThickness = 0.4.toAbsolute()
                                                borderColor = { state: Map<String, Any> ->
                                                    if (state["hovered"] as Boolean || state["clicked"] as Boolean) {
                                                        this@DefaultTheme.selectedBorderColor.value
                                                    } else {
                                                        this@DefaultTheme.borderColor.value
                                                    }
                                                }.toDependent()
                                                backgroundCornerRadius = 0.015.toRelative()

                                                children += Container()
                                                    .apply {
                                                        width = 0.5.toRelative()
                                                        height = 1.0.toCopy()

                                                        backgroundImage = "sorus/ui/themes/add.png".toAbsolute()
                                                        backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                    }

                                                onClick = { state ->
                                                    setting.setting.value.add(ArrayList())
                                                    state["hasInitList"] = false
                                                }
                                            }
                                    }
                            }
                    }
            }
            is DisplayedSetting.Dependent<*> -> {
                return (getSetting(setting.configurableData))
                    .apply {
                        onUpdate += { state ->
                            if (setting.setting.value != setting.expectedValue) {
                                state["hidden"] = true
                            }
                        }
                    }
            }
            else -> return null!!
        }
    }

    override fun initialize() {
        initialized = true

        mainGui = MainGui(this)
        searchGui = SearchUI(this)
        colorPickerGui = ColorPickerUI(this)
        notificationsUi = NotificationsUI(this)

        if (openedNotificationsUI != null) {
            ContainerRenderer.close(openedNotificationsUI!!)
        }

        ContainerRenderer.containers += notificationsUi
        openedNotificationsUI = notificationsUi

        items["mainGui"] = mainGui
        items["searchGui"] = searchGui

        DefaultThemeInitializeEvent(this).call()
    }

    private var openedNotificationsUI: Container? = null

    override fun onOpenGui(id: String, vararg arguments: Any) {
        AdapterManager.adapter.renderer.loadBlur()

        when (id) {
            "mainGui" -> {
                var setTab = false
                var resetSettingsScreen = true
                if (arguments.isNotEmpty()) {
                    when (arguments[0]) {
                        is String -> {
                            mainGui.apply {
                                children[0].apply {
                                    setTab = true
                                    runtime.setState("tab", arguments[0])
                                }
                            }
                        }
                        is DisplayedCategory -> {
                            mainGui.apply {
                                children[0].apply {
                                    setTab = true
                                    runtime.setState("tab", "settings")
                                    runtime.setState("currentSettingsCategory", arguments[0] as DisplayedCategory)
                                    resetSettingsScreen = false
                                    (arguments[0] as DisplayedCategory).onShow()
                                }
                            }
                        }
                    }
                }

                mainGui.apply {
                    children[0].apply {
                        runtime.setState("resetSettingsScreen", resetSettingsScreen)
                        if (!setTab) {
                            runtime.setState("tab", "home")
                        }
                    }
                }
            }
        }
    }

    override fun closeGui() {
        mainGui.apply {
            children[0].apply {
                if (runtime.getState("currentSettingsCategory") != null) {
                    (runtime.getState("currentSettingsCategory") as DisplayedCategory).onHide()
                }
            }
        }

        AdapterManager.adapter.renderer.unloadBlur()
    }

}