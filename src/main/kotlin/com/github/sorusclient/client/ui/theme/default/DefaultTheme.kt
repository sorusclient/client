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
import com.github.sorusclient.client.plugin.PluginManager
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting
import com.github.sorusclient.client.social.SocialManager
import com.github.sorusclient.client.ui.*
import com.github.sorusclient.client.ui.framework.*
import com.github.sorusclient.client.ui.framework.constraint.*
import com.github.sorusclient.client.ui.theme.DefaultThemeInitializeEvent
import com.github.sorusclient.client.ui.theme.Theme
import com.github.sorusclient.client.ui.theme.ThemeManager
import com.github.sorusclient.client.util.AssetUtil
import com.github.sorusclient.client.util.Color
import com.github.sorusclient.client.util.MojangUtil
import com.github.sorusclient.client.websocket.WebSocketManager
import org.apache.commons.io.FileUtils
import org.json.JSONObject
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.net.URL
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class DefaultTheme: Theme() {

    private val backgroundColor: Setting<Color>
    private val borderColor: Setting<Color>
    private val midgroundColor: Setting<Color>
    private val selectedColor: Setting<Color>
    private val selectedBorderColor: Setting<Color>
    private val elementColor: Setting<Color>

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

    private fun getSetting(setting: DisplayedSetting): Container {
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
        mainGui = Container()
            .apply {
                children += Container()
                    .apply {
                        height = 0.8.toRelative()
                        width = 0.8.toRelative()

                        children += Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 0.12.toCopy()
                                backgroundCornerRadius = 0.0155.toRelative()
                                setPadding(0.0125.toRelative())

                                backgroundColor = { this@DefaultTheme.backgroundColor.value }.toDependent()
                                borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                borderThickness = 0.4.toAbsolute()

                                onUpdate += { state ->
                                    val displayedCategory = state["currentSettingsCategory"] as DisplayedCategory?

                                    state["hidden"] = if (displayedCategory != null && state["tab"] == "settings") { !displayedCategory.showUI } else { false }
                                }

                                children += List(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                    .apply {
                                        addChild(Container()
                                            .apply {
                                                width = 0.6.toRelative()
                                                height = 1.0.toCopy()
                                                setPadding(0.2.toRelative())

                                                backgroundImage = "sorus/ui/sorus.png".toAbsolute()
                                                backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                            })

                                        addChild(Container()
                                            .apply {
                                                width = 0.8.toRelative()
                                                height = 0.6.toAbsolute()
                                                setPadding(0.1.toRelative())

                                                backgroundColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                            })

                                        val tabs = arrayOf("home", "settings", "plugins", "themes", "social")

                                        for (tab in tabs) {
                                            addChild(Container()
                                                .apply {
                                                    width = 0.65.toRelative()
                                                    height = 1.0.toCopy()
                                                    setPadding(0.175.toRelative())

                                                    backgroundCornerRadius = 0.15.toRelative()

                                                    backgroundColor = Dependent { state ->
                                                        return@Dependent if (state["tab"] == tab || (tab == "home" && state["tab"] == null)) {
                                                            { this@DefaultTheme.selectedColor.value }.toDependent()
                                                        } else {
                                                            { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                        }
                                                    }

                                                    borderColor = Dependent { state ->
                                                        return@Dependent if (state["tab"] == tab || (tab == "home" && state["tab"] == null) || state["hovered"] as Boolean) {
                                                            { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                        } else {
                                                            { this@DefaultTheme.borderColor.value }.toDependent()
                                                        }
                                                    }

                                                    borderThickness = 0.4.toAbsolute()

                                                    children += Container()
                                                        .apply {
                                                            width = 0.5.toRelative()
                                                            height = 0.5.toRelative()

                                                            backgroundImage = "sorus/ui/navbar/$tab.png".toAbsolute()
                                                            backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                        }

                                                    onClick = { state ->
                                                        state["tab"] = tab
                                                        if (tab == "settings") {
                                                            state["resetSettingsScreen"] = true
                                                        }
                                                    }
                                                })
                                        }
                                    }
                            }

                        children += TabHolder()
                            .apply {
                                stateId = "tab"
                                defaultTab = "home"

                                addChild("home", Container())

                                addChild("settings", Container()
                                    .apply {
                                        onUpdate += { state ->
                                            val displayedCategory = state["currentSettingsCategory"] as DisplayedCategory

                                            state["hidden"] = !displayedCategory.showUI

                                            if (displayedCategory.`return`) {
                                                displayedCategory.onHide()
                                                state["currentSettingsCategory"] = displayedCategory.parent!!

                                                displayedCategory.`return` = false
                                            }

                                            if (displayedCategory.wantedOpenCategory != null) {
                                                displayedCategory.onHide()
                                                state["currentSettingsCategory"] = displayedCategory.wantedOpenCategory!!
                                                displayedCategory.wantedOpenCategory = null
                                            }

                                            if (displayedCategory.customUI != null) {
                                                displayedCategory.onHide()
                                                state["tab"] = "custom"
                                                state["customContainer"] = displayedCategory.customUI!!
                                                displayedCategory.customUI = null
                                            }
                                        }

                                        onClose += { state ->
                                            val displayedCategory = state["currentSettingsCategory"] as DisplayedCategory
                                            displayedCategory.onHide()
                                        }

                                        onInit += { state ->
                                            if (state.second["resetSettingsScreen"] == null || state.second["resetSettingsScreen"] as Boolean) {
                                                state.second["currentSettingsCategory"] = SettingManager.mainUICategory
                                            }
                                        }

                                        children += Container()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = 0.3.toRelative()

                                                backgroundCornerRadius = 0.0155.toRelative()
                                                setPadding(0.0125.toRelative())
                                                paddingLeft = 0.0.toAbsolute()

                                                backgroundColor = { this@DefaultTheme.backgroundColor.value }.toDependent()
                                                borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                borderThickness = 0.4.toAbsolute()

                                                onInit += {
                                                    clear()

                                                    children += Container()
                                                        .apply {
                                                            y = Side.NEGATIVE.toSide()
                                                            height = 0.035.toRelative()
                                                            setPadding(0.05.toRelative())

                                                            children += Text()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()

                                                                    fontRenderer = "sorus/ui/font/Quicksand-Bold.ttf".toAbsolute()
                                                                    scale = 0.0075.toRelative()
                                                                    text = "Profiles".toAbsolute()
                                                                    textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                }
                                                        }

                                                    children += Container()
                                                        .apply {
                                                            y = Side.NEGATIVE.toSide()
                                                            height = 0.125.toRelative()

                                                            setPadding(0.05.toRelative())

                                                            children += Container()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()

                                                                    width = 1.0.toCopy()
                                                                    height = 0.8.toRelative()
                                                                    setPadding(Relative(0.1, true))

                                                                    backgroundCornerRadius = 0.03.toRelative()
                                                                    backgroundImage = "sorus/ui/sorus2.png".toAbsolute()
                                                                }

                                                            children += Container()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()
                                                                    width = 0.0125.toRelative()
                                                                }

                                                            children += Text()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()
                                                                    y = Side.NEGATIVE.toSide()

                                                                    setPadding(Relative(0.1, true))
                                                                    scale = 0.009.toRelative()
                                                                    fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()


                                                                    var name = SettingManager.currentProfile!!.id
                                                                    name = name.substring(name.lastIndexOf('/') + 1, name.length)

                                                                    if (name.isEmpty()) {
                                                                        name = "Main"
                                                                    }

                                                                    text = name.toAbsolute()
                                                                    textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                }
                                                        }

                                                    children += Container()
                                                        .apply {
                                                            y = Side.NEGATIVE.toSide()
                                                            height = 0.06125.toRelative()
                                                            setPadding(0.05.toRelative())

                                                            children += Container()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()
                                                                    width = 0.475.toRelative()

                                                                    backgroundCornerRadius = 0.025.toRelative()
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

                                                                    children += Container()
                                                                        .apply {
                                                                            x = Side.NEGATIVE.toSide()
                                                                            width = 1.0.toCopy()
                                                                            height = 0.5.toRelative()
                                                                            setPadding(Relative(0.2, true))

                                                                            backgroundImage = "sorus/ui/profiles/create.png".toAbsolute()
                                                                            backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                        }

                                                                    children += Text()
                                                                        .apply {
                                                                            x = Side.NEGATIVE.toSide()
                                                                            setPadding(Relative(0.2, true))

                                                                            scale = 0.012.toRelative()
                                                                            fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                            text = "Create".toAbsolute()
                                                                            textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                        }

                                                                    onClick = { state ->
                                                                        SettingManager.createNewProfile(SettingManager.currentProfile!!)
                                                                        state["hasInitProfiles"] = false
                                                                    }
                                                                }

                                                            children += Container()
                                                                .apply {
                                                                    x = Side.POSITIVE.toSide()
                                                                    width = 0.475.toRelative()

                                                                    backgroundCornerRadius = 0.025.toRelative()
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

                                                                    children += Container()
                                                                        .apply {
                                                                            x = Side.NEGATIVE.toSide()
                                                                            width = 1.0.toCopy()
                                                                            height = 0.5.toRelative()
                                                                            setPadding(Relative(0.2, true))

                                                                            backgroundImage = "sorus/ui/profiles/delete.png".toAbsolute()
                                                                            backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                        }

                                                                    children += Text()
                                                                        .apply {
                                                                            x = Side.NEGATIVE.toSide()
                                                                            y = Side.ZERO.toSide()
                                                                            setPadding(Relative(0.2, true))

                                                                            scale = 0.012.toRelative()
                                                                            fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                            text = "Delete".toAbsolute()
                                                                            textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                        }

                                                                    onClick = { state ->
                                                                        SettingManager.delete(SettingManager.currentProfile!!)
                                                                        state["hasInitProfiles"] = false
                                                                    }
                                                                }
                                                        }

                                                    children += Container()
                                                        .apply {
                                                            y = Side.NEGATIVE.toSide()
                                                            width = 0.8.toRelative()
                                                            height = 0.6.toAbsolute()
                                                            setPadding(0.05.toRelative())

                                                            backgroundColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                        }

                                                    children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                                        .apply {
                                                            setPadding(0.075.toRelative())

                                                            onInit += {
                                                                clear()

                                                                for (profile in UserInterface.getProfiles()) {
                                                                    addChild(Container()
                                                                        .apply {
                                                                            height = 0.15.toCopy()

                                                                            children += Container()
                                                                                .apply {
                                                                                    x = Side.POSITIVE.toSide()
                                                                                    width = (1.0 - profile.second * 0.1).toRelative()

                                                                                    backgroundCornerRadius = 0.035.toRelative()
                                                                                    borderThickness = 0.4.toAbsolute()

                                                                                    backgroundColor = Dependent {
                                                                                        return@Dependent if (SettingManager.currentProfile == profile.first) {
                                                                                            { this@DefaultTheme.selectedColor.value }.toDependent()
                                                                                        } else {
                                                                                            { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                                        }
                                                                                    }

                                                                                    borderColor = Dependent { state ->
                                                                                        return@Dependent if (SettingManager.currentProfile == profile.first || state["hovered"] as Boolean) {
                                                                                            { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                                                        } else {
                                                                                            Color.fromRGB(10, 10, 10, 150)
                                                                                        }
                                                                                    }

                                                                                    children += Container()
                                                                                        .apply {
                                                                                            x = Side.NEGATIVE.toSide()
                                                                                            width = 1.0.toCopy()
                                                                                            height = 0.6.toRelative()
                                                                                            setPadding(Relative(0.2, true))

                                                                                            backgroundCornerRadius = 0.02.toRelative()
                                                                                            backgroundImage = "sorus/ui/sorus2.png".toAbsolute()
                                                                                        }

                                                                                    children += Text()
                                                                                        .apply {
                                                                                            x = Side.NEGATIVE.toSide()

                                                                                            scale = 0.009.toRelative()
                                                                                            fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()

                                                                                            var name = profile.first.id
                                                                                            name = name.substring(0, name.length - 1)

                                                                                            name = if (name.isEmpty()) {
                                                                                                "Main"
                                                                                            } else {
                                                                                                name.substring(
                                                                                                    name.lastIndexOf('/') + 1,
                                                                                                    name.length
                                                                                                )
                                                                                            }

                                                                                            text = name.toAbsolute()
                                                                                            textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                        }

                                                                                    onClick = {
                                                                                        SettingManager.load(profile.first)
                                                                                    }
                                                                                }
                                                                        })

                                                                    addChild(Container()
                                                                        .apply {
                                                                            height = 0.05.toCopy()
                                                                        })
                                                                }
                                                            }

                                                            for (onInit in onInit) {
                                                                onInit(Pair(this, HashMap()))
                                                            }

                                                            onStateUpdate["hasInitProfiles"] = { state ->
                                                                if (state["hasInitProfiles"] == false) {
                                                                    state["hasInitProfiles"] = true
                                                                    state["hasInit"] = false
                                                                }
                                                            }
                                                        }
                                                }

                                                for (onInit in onInit) {
                                                    onInit(Pair(this, HashMap()))
                                                }

                                                storedState += "hasInitProfiles"
                                            }

                                        children += Container()
                                            .apply {
                                                backgroundCornerRadius = 0.0155.toRelative()
                                                setPadding(0.0125.toRelative())

                                                backgroundColor = { this@DefaultTheme.backgroundColor.value }.toDependent()
                                                borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                borderThickness = 0.4.toAbsolute()

                                                storedState += "hasInitSettings"

                                                onStateUpdate["hasInitSettings"] = { state ->
                                                    if (state["hasInitSettings"] == false) {
                                                        state["hasInitSettings"] = true
                                                        state["hasInit"] = false
                                                    }
                                                }

                                                onInit += { state ->
                                                    if (state.second["currentSettingsCategory"] == null) {
                                                        state.second["currentSettingsCategory"] = SettingManager.mainUICategory
                                                    }

                                                    val category = state.second["currentSettingsCategory"] as DisplayedCategory

                                                    children.clear()

                                                    children += Container()
                                                        .apply {
                                                            y = Side.NEGATIVE.toSide()
                                                            height = 0.05.toRelative()
                                                            setPadding(0.025.toRelative())

                                                            children += Text()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()
                                                                    y = Side.NEGATIVE.toSide()

                                                                    fontRenderer = "sorus/ui/font/Quicksand-Bold.ttf".toAbsolute()
                                                                    scale = 0.0031.toRelative()
                                                                    text = "Settings | ${category.displayName}".toAbsolute()
                                                                    textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                }

                                                            if (category.parent != null) {
                                                                children += Container()
                                                                    .apply {
                                                                        x = Side.POSITIVE.toSide()
                                                                        width = 1.0.toCopy()
                                                                        height = 0.98.toRelative()

                                                                        backgroundCornerRadius = 0.005.toRelative()
                                                                        setPadding(Relative(0.01, true))

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

                                                                        children += Container()
                                                                            .apply {
                                                                                width = 0.5.toRelative()
                                                                                height = 0.5.toRelative()

                                                                                backgroundImage = "sorus/ui/settings/back.png".toAbsolute()
                                                                                backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                            }

                                                                        onClick = { state ->
                                                                            state["currentSettingsCategory"] = category.parent!!
                                                                            state["hasInitSettings"] = false
                                                                            category.onHide()
                                                                            category.parent!!.onShow()
                                                                        }
                                                                    }
                                                            }
                                                        }

                                                    children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                                        .apply {
                                                            addChild(
                                                                List(com.github.sorusclient.client.ui.framework.List.GRID)
                                                                .apply {
                                                                    columns = 3

                                                                    var count = 0
                                                                    for (displayed in category.components) {
                                                                        if (displayed is DisplayedCategory) {
                                                                            count++
                                                                        }
                                                                    }

                                                                    height = Relative(ceil(count / 3.0) * 0.06 + (ceil(count / 3.0) + 1) * 0.015, true)

                                                                    for (displayed in category.components) {
                                                                        if (displayed is DisplayedCategory) {
                                                                            addChild(Container()
                                                                                .apply {
                                                                                    width = 0.31666.toRelative()
                                                                                    height = 0.2.toCopy()

                                                                                    backgroundCornerRadius = 0.0155.toRelative()
                                                                                    setPadding(0.0125.toRelative())

                                                                                    backgroundColor = Dependent { state ->
                                                                                        if (state["clicked"] != null && state["clicked"] as Boolean) {
                                                                                            { this@DefaultTheme.selectedColor.value }.toDependent()
                                                                                        } else {
                                                                                            { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                                        }
                                                                                    }
                                                                                    borderThickness = 0.4.toAbsolute()
                                                                                    borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                                                    borderColor = Dependent { state ->
                                                                                        if ((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) {
                                                                                            { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                                                        } else {
                                                                                            { this@DefaultTheme.borderColor.value }.toDependent()
                                                                                        }
                                                                                    }

                                                                                    children += Container()
                                                                                        .apply {
                                                                                            x = Side.NEGATIVE.toSide()
                                                                                            width = 1.0.toCopy()
                                                                                            height = 0.6.toRelative()
                                                                                            setPadding(Relative(0.2, true))
                                                                                        }

                                                                                    children += Text()
                                                                                        .apply {
                                                                                            x = Side.NEGATIVE.toSide()

                                                                                            scale = 0.006.toRelative()
                                                                                            fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                                            text = displayed.displayName.toAbsolute()
                                                                                            textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                        }

                                                                                    onClick = { state ->
                                                                                        displayed.onShow()
                                                                                        state["currentSettingsCategory"] = displayed
                                                                                        state["hasInitSettings"] = false
                                                                                    }
                                                                                })
                                                                        }
                                                                    }
                                                                })

                                                            addChild(Container()
                                                                .apply {
                                                                    width = 0.9.toRelative()
                                                                    height = 0.6.toAbsolute()

                                                                    backgroundColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                                })

                                                            for (setting in category.components) {
                                                                if (setting is DisplayedSetting) {
                                                                    addChild(getSetting(setting)
                                                                        .apply {
                                                                            children += Container()
                                                                                .apply {
                                                                                    x = Side.POSITIVE.toSide()
                                                                                    width = 0.15.toRelative()
                                                                                    height = 0.225.toCopy()

                                                                                    backgroundCornerRadius = 0.01.toRelative()
                                                                                    setPadding(0.1.toRelative())

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

                                                                                    children += Text()
                                                                                        .apply {
                                                                                            scale = 0.011.toRelative()
                                                                                            fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                                                                            text = "Reset".toAbsolute()
                                                                                            textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                        }

                                                                                    onUpdate += { state ->
                                                                                        var settingNew = setting
                                                                                        while (settingNew is DisplayedSetting.Dependent<*>) {
                                                                                            settingNew = settingNew.configurableData
                                                                                        }

                                                                                        if (settingNew is DisplayedSetting.ConfigurableDataSingleSetting<*>) {
                                                                                            state["hidden"] = !(settingNew.setting.overriden && (SettingManager.currentProfile != SettingManager.mainProfile || settingNew.setting.realValue != settingNew.setting.defaultValue)) || settingNew.setting.isForcedValue
                                                                                        }
                                                                                    }

                                                                                    onClick = {
                                                                                        var settingNew = setting
                                                                                        while (settingNew is DisplayedSetting.Dependent<*>) {
                                                                                            settingNew = (settingNew as DisplayedSetting.Dependent<*>).configurableData
                                                                                        }

                                                                                        if (settingNew is DisplayedSetting.ConfigurableDataSingleSetting<*>) {
                                                                                            if (SettingManager.currentProfile == SettingManager.mainProfile) {
                                                                                                (settingNew as DisplayedSetting.ConfigurableDataSingleSetting<*>).setting.setValueRaw((settingNew as DisplayedSetting.ConfigurableDataSingleSetting<*>).setting.defaultValue!!)
                                                                                            } else {
                                                                                                (settingNew as DisplayedSetting.ConfigurableDataSingleSetting<*>).setting.overriden = false
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                        })
                                                                }
                                                            }
                                                        }
                                                }

                                                for (onInit in onInit) {
                                                    onInit(Pair(this, HashMap()))
                                                }
                                            }
                                    })

                                addChild("plugins", Container()
                                    .apply {
                                        if (true) return@apply

                                        storedState += "hasInitPlugins"
                                        onStateUpdate["hasInitPlugins"] = { state ->
                                            if (state["hasInitPlugins"] != null && state["hasInitPlugins"] == false) {
                                                state["hasInitPlugins"] = true
                                                state["hasInit"] = false
                                            }
                                        }

                                        children += Container()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = 0.475.toRelative()

                                                setPadding(0.0125.toRelative())
                                                paddingLeft = 0.0.toAbsolute()
                                                backgroundCornerRadius = 0.0155.toRelative()

                                                backgroundColor = { this@DefaultTheme.backgroundColor.value }.toDependent()
                                                borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                borderThickness = 0.425.toAbsolute()
                                                onInit += {
                                                    clear()

                                                    children += Container()
                                                        .apply {
                                                            y = Side.NEGATIVE.toSide()
                                                            height = 0.035.toRelative()
                                                            setPadding(0.035.toRelative())
                                                            paddingBottom = 0.0.toAbsolute()

                                                            children += Text()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()

                                                                    fontRenderer = "sorus/ui/font/Quicksand-Bold.ttf".toAbsolute()
                                                                    scale = 0.0045.toRelative()
                                                                    text = "Loaded Plugins".toAbsolute()
                                                                    textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                }
                                                        }

                                                    children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                                        .apply {
                                                            for (plugin in PluginManager.getPlugins()) {
                                                                children += Container()
                                                                    .apply {
                                                                        height = 0.125.toCopy()
                                                                        setPadding(0.025.toRelative())

                                                                        backgroundCornerRadius = 0.025.toRelative()
                                                                        borderThickness = 0.4.toAbsolute()

                                                                        backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                        borderColor = { this@DefaultTheme.borderColor.value }.toDependent()

                                                                        children += Container()
                                                                            .apply {
                                                                                x = Side.NEGATIVE.toSide()
                                                                                width = 1.0.toCopy()
                                                                                setPadding(Relative(0.15, true))

                                                                                backgroundCornerRadius = 0.02.toRelative()
                                                                                backgroundImage = plugin.logo.toAbsolute()
                                                                            }

                                                                        children += Text()
                                                                            .apply {
                                                                                x = Side.NEGATIVE.toSide()
                                                                                y = Side.NEGATIVE.toSide()
                                                                                setPadding(Relative(0.175, true))

                                                                                fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                                scale = 0.005.toRelative()
                                                                                text = plugin.name.toAbsolute()
                                                                                textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                            }

                                                                        children += Text()
                                                                            .apply {
                                                                                x = Side.NEGATIVE.toSide()
                                                                                y = Side.POSITIVE.toSide()
                                                                                setPadding(Relative(0.175, true))

                                                                                fontRenderer = "sorus/ui/font/Quicksand-Regular.ttf".toAbsolute()
                                                                                scale = 0.0035.toRelative()
                                                                                text = plugin.description.toAbsolute()
                                                                                textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                            }

                                                                        children += Container()
                                                                            .apply {
                                                                                x = Side.POSITIVE.toSide()
                                                                                width = 1.0.toCopy()

                                                                                setPadding(Relative(0.2, true))

                                                                                backgroundCornerRadius = 0.015.toRelative()
                                                                                borderThickness = 0.4.toAbsolute()

                                                                                backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()

                                                                                borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                                                borderColor = { state: Map<String, Any> ->
                                                                                    if (state["hovered"] as Boolean) {
                                                                                        { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                                                    } else {
                                                                                        Color.fromRGB(10, 10, 10, 150)
                                                                                    }
                                                                                }.toDependent()

                                                                                children += Container()
                                                                                    .apply {
                                                                                        width = 0.5.toRelative()
                                                                                        height = 1.0.toCopy()

                                                                                        backgroundImage = "sorus/ui/profiles/delete.png".toAbsolute()
                                                                                        backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                    }

                                                                                onClick = { state ->
                                                                                    PluginManager.remove(plugin)
                                                                                    //UserInterface.javaClass.classLoader.javaClass.getMethod("removeURL", URL::class.java).invoke(
                                                                                    //    UserInterface.javaClass.classLoader, plugin.file.toURI().toURL())
                                                                                    //(GlassLoader.getInstance() as GlassLoaderImpl).preLoad()
                                                                                    //(GlassLoader.getInstance() as GlassLoaderImpl).loadUpdateShards()
                                                                                    state["hasInitPlugins"] = false
                                                                                }
                                                                            }
                                                                    }
                                                            }
                                                        }
                                                }

                                                for (onInit in onInit) {
                                                    onInit(Pair(this, HashMap()))
                                                }
                                            }

                                        children += Container()
                                            .apply {
                                                x = Side.POSITIVE.toSide()

                                                setPadding(0.0125.toRelative())
                                                backgroundCornerRadius = 0.0155.toRelative()

                                                backgroundColor = { this@DefaultTheme.backgroundColor.value }.toDependent()
                                                borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                borderThickness = 0.4.toAbsolute()

                                                val pluginsFull = ArrayList<Triple<String, Map<String, ByteArray>, JSONObject>>()
                                                for (plugin in AssetUtil.getAllPlugins()) {
                                                    val pluginJarData = AssetUtil.getPluginData(plugin)
                                                    val pluginJson = JSONObject(String(pluginJarData["plugin.json"]!!))
                                                    pluginsFull.add(Triple(plugin, pluginJarData, pluginJson))
                                                }

                                                onInit += {
                                                    clear()

                                                    children += Container()
                                                        .apply {
                                                            y = Side.NEGATIVE.toSide()
                                                            height = 0.035.toRelative()
                                                            setPadding(0.035.toRelative())
                                                            paddingBottom = 0.0.toAbsolute()

                                                            children += Text()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()

                                                                    fontRenderer = "sorus/ui/font/Quicksand-Bold.ttf".toAbsolute()
                                                                    scale = 0.0045.toRelative()
                                                                    text = "Available Plugins".toAbsolute()
                                                                    textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                }
                                                        }

                                                    children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                                        .apply {
                                                            for (plugin in pluginsFull) {
                                                                if (PluginManager.getPlugins().any { it.id == plugin.first }) continue

                                                                val logoData = plugin.second[plugin.third["logo"]!!]!!
                                                                AdapterManager.adapter.renderer.createTexture("plugin-${plugin.first}", logoData, true)

                                                                children += Container()
                                                                    .apply {
                                                                        height = 0.125.toCopy()
                                                                        setPadding(0.025.toRelative())

                                                                        backgroundCornerRadius = 0.025.toRelative()
                                                                        borderThickness = 0.4.toAbsolute()

                                                                        backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                        borderColor = { this@DefaultTheme.borderColor.value }.toDependent()

                                                                        children += Container()
                                                                            .apply {
                                                                                x = Side.NEGATIVE.toSide()
                                                                                width = 1.0.toCopy()
                                                                                setPadding(Relative(0.15, true))

                                                                                backgroundCornerRadius = 0.025.toRelative()
                                                                                backgroundImage = "plugin-${plugin.first}".toAbsolute()
                                                                            }

                                                                        children += Text()
                                                                            .apply {
                                                                                x = Side.NEGATIVE.toSide()
                                                                                y = Side.NEGATIVE.toSide()
                                                                                setPadding(Relative(0.175, true))

                                                                                fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                                scale = 0.005.toRelative()
                                                                                text = (plugin.third["name"] as String?).toAbsolute()
                                                                                textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                            }

                                                                        children += Text()
                                                                            .apply {
                                                                                x = Side.NEGATIVE.toSide()
                                                                                y = Side.POSITIVE.toSide()
                                                                                setPadding(Relative(0.175, true))

                                                                                fontRenderer = "sorus/ui/font/Quicksand-Regular.ttf".toAbsolute()
                                                                                scale = 0.0035.toRelative()
                                                                                text = (plugin.third["description"] as String?).toAbsolute()
                                                                                textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                            }

                                                                        children += Container()
                                                                            .apply {
                                                                                x = Side.POSITIVE.toSide()
                                                                                width = 1.0.toCopy()

                                                                                setPadding(Relative(0.2, true))

                                                                                backgroundCornerRadius = 0.015.toRelative()
                                                                                borderThickness = 0.4.toAbsolute()

                                                                                backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                                borderColor = { state: Map<String, Any> ->
                                                                                    if (state["hovered"] as Boolean) {
                                                                                        { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                                                    } else {
                                                                                        Color.fromRGB(10, 10, 10, 150)
                                                                                    }
                                                                                }.toDependent()

                                                                                children += Container()
                                                                                    .apply {
                                                                                        width = 0.5.toRelative()
                                                                                        height = 1.0.toCopy()

                                                                                        backgroundImage = "sorus/ui/plugins/download.png".toAbsolute()
                                                                                        backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                    }

                                                                                onClick = { state ->
                                                                                    val url = URL("${AssetUtil.basePluginsUrl}/$plugin.jar")
                                                                                    FileUtils.copyInputStreamToFile(url.openStream(), File("shards/$plugin.jar"))
                                                                                    //(GlassLoader.getInstance() as GlassLoaderImpl).preLoad()
                                                                                    //(GlassLoader.getInstance() as GlassLoaderImpl).loadUpdateShards()
                                                                                    PluginManager.findPlugins()
                                                                                    state["hasInitPlugins"] = false
                                                                                }
                                                                            }
                                                                    }
                                                            }
                                                        }
                                                }

                                                for (onInit in onInit) {
                                                    onInit(Pair(this, HashMap()))
                                                }
                                            }
                                    })

                                addChild("themes", Container()
                                    .apply {
                                        storedState += "hasInitThemes"
                                        onStateUpdate["hasInitThemes"] = { state ->
                                            if (state["hasInitThemes"] != null && state["hasInitThemes"] == false) {
                                                state["hasInitThemes"] = true
                                                state["hasInit"] = false
                                            }
                                        }

                                        children += TabHolder()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = 0.475.toRelative()

                                                setPadding(0.0125.toRelative())
                                                paddingLeft = 0.0.toAbsolute()
                                                backgroundCornerRadius = 0.0155.toRelative()

                                                backgroundColor = { this@DefaultTheme.backgroundColor.value }.toDependent()
                                                borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                borderThickness = 0.425.toAbsolute()

                                                storedState += "themeScreen"
                                                storedState += "editedTheme"

                                                defaultTab = "themeList"
                                                stateId = "themeScreen"

                                                addChild("themeList", Container()
                                                    .apply {
                                                        onInit += {
                                                            clear()

                                                            children += Container()
                                                                .apply {
                                                                    y = Side.NEGATIVE.toSide()
                                                                    height = 0.035.toRelative()
                                                                    setPadding(0.035.toRelative())
                                                                    paddingBottom = 0.0.toAbsolute()

                                                                    children += Text()
                                                                        .apply {
                                                                            x = Side.NEGATIVE.toSide()

                                                                            fontRenderer = "sorus/ui/font/Quicksand-Bold.ttf".toAbsolute()
                                                                            scale = 0.005.toRelative()
                                                                            text = "Themes".toAbsolute()
                                                                            textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                        }
                                                                }

                                                            children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                                                .apply {
                                                                    for (theme in ThemeManager.configuredThemes) {
                                                                        children += Container()
                                                                            .apply {
                                                                                height = 0.125.toCopy()
                                                                                setPadding(0.025.toRelative())

                                                                                backgroundCornerRadius = 0.025.toRelative()
                                                                                borderThickness = 0.4.toAbsolute()

                                                                                backgroundColor = {
                                                                                    if (false) {
                                                                                        this@DefaultTheme.selectedColor.value
                                                                                    } else {
                                                                                        this@DefaultTheme.midgroundColor.value
                                                                                    }
                                                                                }.toDependent()

                                                                                borderColor = {
                                                                                    if (false) {
                                                                                        this@DefaultTheme.selectedBorderColor.value
                                                                                    } else {
                                                                                        this@DefaultTheme.borderColor.value
                                                                                    }
                                                                                }.toDependent()

                                                                                children += Container()
                                                                                    .apply {
                                                                                        x = Side.NEGATIVE.toSide()
                                                                                        width = 1.0.toCopy()
                                                                                        setPadding(Relative(0.15, true))

                                                                                        backgroundCornerRadius = 0.02.toRelative()
                                                                                        backgroundImage = ThemeManager.registeredThemes[theme.javaClass]!!.second.toAbsolute()
                                                                                    }

                                                                                children += Text()
                                                                                    .apply {
                                                                                        x = Side.NEGATIVE.toSide()
                                                                                        y = Side.NEGATIVE.toSide()
                                                                                        setPadding(Relative(0.175, true))

                                                                                        fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                                        scale = 0.005.toRelative()
                                                                                        text = ThemeManager.registeredThemes[theme.javaClass]!!.first.toAbsolute()
                                                                                        textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                    }

                                                                                children += Container()
                                                                                    .apply {
                                                                                        x = Side.POSITIVE.toSide()
                                                                                        width = 1.0.toCopy()

                                                                                        setPadding(Relative(0.2, true))

                                                                                        backgroundCornerRadius = 0.015.toRelative()
                                                                                        borderThickness = 0.4.toAbsolute()

                                                                                        backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()

                                                                                        borderColor = { state: Map<String, Any> ->
                                                                                            if (state["hovered"] as Boolean && ThemeManager.configuredThemes.indexOf(theme) < ThemeManager.configuredThemes.size - 1) {
                                                                                                { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                                                            } else {
                                                                                                { this@DefaultTheme.borderColor.value }.toDependent()
                                                                                            }
                                                                                        }.toDependent()

                                                                                        children += Container()
                                                                                            .apply {
                                                                                                width = 0.5.toRelative()
                                                                                                height = 1.0.toCopy()

                                                                                                backgroundImage = "sorus/ui/profiles/delete.png".toAbsolute()
                                                                                                backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                            }

                                                                                        onClick = { state ->
                                                                                            if (ThemeManager.configuredThemes.indexOf(theme) < ThemeManager.configuredThemes.size - 1) {
                                                                                                ThemeManager.configuredThemes.remove(theme)
                                                                                                ThemeManager.closeGui()
                                                                                                ThemeManager.openMenuGui("themes")
                                                                                                state["hasInitThemes"] = false
                                                                                            }
                                                                                        }
                                                                                    }

                                                                                children += Container()
                                                                                    .apply {
                                                                                        x = Side.POSITIVE.toSide()
                                                                                        width = 1.0.toCopy()

                                                                                        setPadding(Relative(0.2, true))

                                                                                        backgroundCornerRadius = 0.015.toRelative()
                                                                                        borderThickness = 0.4.toAbsolute()

                                                                                        backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()

                                                                                        borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                                                        borderColor = { state: Map<String, Any> ->
                                                                                            if (state["hovered"] as Boolean) {
                                                                                                { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                                                            } else {
                                                                                                Color.fromRGB(10, 10, 10, 150)
                                                                                            }
                                                                                        }.toDependent()

                                                                                        children += Container()
                                                                                            .apply {
                                                                                                width = 0.5.toRelative()
                                                                                                height = 1.0.toCopy()

                                                                                                backgroundImage = "sorus/ui/navbar/settings.png".toAbsolute()
                                                                                                backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                            }

                                                                                        onClick = { state ->
                                                                                            state["editedTheme"] = theme
                                                                                            state["themeScreen"] = "themeEdit"
                                                                                        }
                                                                                    }

                                                                                children += Container()
                                                                                    .apply {
                                                                                        x = Side.POSITIVE.toSide()
                                                                                        width = 1.0.toCopy()

                                                                                        setPadding(Relative(0.2, true))

                                                                                        children += Container()
                                                                                            .apply {
                                                                                                y = Side.NEGATIVE.toSide()
                                                                                                height = 0.45.toRelative()

                                                                                                backgroundCornerRadius = 0.1375.toRelative()
                                                                                                borderThickness = 0.4.toAbsolute()

                                                                                                backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()

                                                                                                borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                                                                borderColor = { state: Map<String, Any> ->
                                                                                                    if (state["hovered"] as Boolean && ThemeManager.configuredThemes.indexOf(theme) > 0) {
                                                                                                        { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                                                                    } else {
                                                                                                        Color.fromRGB(10, 10, 10, 150)
                                                                                                    }
                                                                                                }.toDependent()

                                                                                                onClick = { state ->
                                                                                                    val index = ThemeManager.configuredThemes.indexOf(theme)
                                                                                                    ThemeManager.configuredThemes.removeAt(index)
                                                                                                    ThemeManager.configuredThemes.add(max(0, index - 1), theme)

                                                                                                    ThemeManager.closeGui()
                                                                                                    ThemeManager.openMenuGui("themes")
                                                                                                    state["hasInitThemes"] = false
                                                                                                }
                                                                                            }

                                                                                        children += Container()
                                                                                            .apply {
                                                                                                y = Side.POSITIVE.toSide()
                                                                                                height = 0.45.toRelative()

                                                                                                backgroundCornerRadius = 0.1375.toRelative()
                                                                                                borderThickness = 0.4.toAbsolute()

                                                                                                backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()

                                                                                                borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                                                                borderColor = { state: Map<String, Any> ->
                                                                                                    if (state["hovered"] as Boolean && ThemeManager.configuredThemes.indexOf(theme) < ThemeManager.configuredThemes.size - 1) {
                                                                                                        { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                                                                    } else {
                                                                                                        Color.fromRGB(10, 10, 10, 150)
                                                                                                    }
                                                                                                }.toDependent()

                                                                                                onClick = { state ->
                                                                                                    val index = ThemeManager.configuredThemes.indexOf(theme)
                                                                                                    ThemeManager.configuredThemes.removeAt(index)
                                                                                                    ThemeManager.configuredThemes.add(min(index + 1, ThemeManager.configuredThemes.size), theme)

                                                                                                    ThemeManager.closeGui()
                                                                                                    ThemeManager.openMenuGui("themes")
                                                                                                    state["hasInitThemes"] = false
                                                                                                }
                                                                                            }
                                                                                    }
                                                                            }
                                                                    }
                                                                }
                                                        }

                                                        for (onInit in onInit) {
                                                            onInit(Pair(this, HashMap()))
                                                        }
                                                    })

                                                addChild("themeEdit", Container()
                                                    .apply {
                                                        storedState += "hasInitSettings"

                                                        onStateUpdate["hasInitSettings"] = { state ->
                                                            if (state["hasInitSettings"] == false) {
                                                                state["hasInitSettings"] = true
                                                                state["hasInit"] = false
                                                            }
                                                        }

                                                        onInit += onInit@{ state ->
                                                            val theme = state.second["editedTheme"] as Theme? ?: return@onInit

                                                            val category = theme.uiCategory

                                                            children.clear()

                                                            children += Container()
                                                                .apply {
                                                                    y = Side.NEGATIVE.toSide()
                                                                    height = 0.05.toRelative()
                                                                    setPadding(0.037.toRelative())

                                                                    children += Text()
                                                                        .apply {
                                                                            x = Side.NEGATIVE.toSide()
                                                                            y = Side.NEGATIVE.toSide()

                                                                            fontRenderer = "sorus/ui/font/Quicksand-Bold.ttf".toAbsolute()
                                                                            scale = 0.005.toRelative()
                                                                            text = "Settings | ${category.displayName}".toAbsolute()
                                                                            textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                        }

                                                                    children += Container()
                                                                        .apply {
                                                                            x = Side.POSITIVE.toSide()
                                                                            width = 1.0.toCopy()
                                                                            height = 0.98.toRelative()

                                                                            backgroundCornerRadius = 0.0075.toRelative()
                                                                            setPadding(Relative(0.01, true))

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

                                                                            children += Container()
                                                                                .apply {
                                                                                    width = 0.5.toRelative()
                                                                                    height = 0.5.toRelative()

                                                                                    backgroundImage = "sorus/ui/settings/back.png".toAbsolute()
                                                                                    backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                }

                                                                            onClick = { state ->
                                                                                state["themeScreen"] = "themeList"
                                                                            }
                                                                        }
                                                                }

                                                            children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                                                .apply {
                                                                    for (setting in category.components) {
                                                                        if (setting is DisplayedSetting) {
                                                                            addChild(getSetting(setting))
                                                                        }
                                                                    }
                                                                }
                                                        }

                                                        for (onInit in onInit) {
                                                            onInit(Pair(this, HashMap()))
                                                        }
                                                    })
                                            }

                                        children += Container()
                                            .apply {
                                                x = Side.POSITIVE.toSide()

                                                setPadding(0.0125.toRelative())
                                                backgroundCornerRadius = 0.0155.toRelative()

                                                backgroundColor = { this@DefaultTheme.backgroundColor.value }.toDependent()
                                                borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                borderThickness = 0.4.toAbsolute()

                                                onInit += {
                                                    clear()

                                                    children += Container()
                                                        .apply {
                                                            y = Side.NEGATIVE.toSide()
                                                            height = 0.035.toRelative()
                                                            setPadding(0.035.toRelative())
                                                            paddingBottom = 0.0.toAbsolute()

                                                            children += Text()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()

                                                                    fontRenderer = "sorus/ui/font/Quicksand-Bold.ttf".toAbsolute()
                                                                    scale = 0.0045.toRelative()
                                                                    text = "Available Themes".toAbsolute()
                                                                    textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                }
                                                        }

                                                    children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                                        .apply {
                                                            for (theme in ThemeManager.registeredThemes) {
                                                                children += Container()
                                                                    .apply {
                                                                        height = 0.125.toCopy()
                                                                        setPadding(0.025.toRelative())

                                                                        backgroundCornerRadius = 0.025.toRelative()
                                                                        borderThickness = 0.4.toAbsolute()

                                                                        backgroundColor =
                                                                            { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                        borderColor =
                                                                            { this@DefaultTheme.borderColor.value }.toDependent()

                                                                        children += Container()
                                                                            .apply {
                                                                                x = Side.NEGATIVE.toSide()
                                                                                width = 1.0.toCopy()
                                                                                setPadding(Relative(0.15, true))

                                                                                backgroundCornerRadius = 0.025.toRelative()
                                                                                backgroundImage = theme.value.second.toAbsolute()
                                                                            }

                                                                        children += Text()
                                                                            .apply {
                                                                                x = Side.NEGATIVE.toSide()
                                                                                y = Side.NEGATIVE.toSide()
                                                                                setPadding(Relative(0.175, true))

                                                                                fontRenderer =
                                                                                    "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                                scale = 0.005.toRelative()
                                                                                text = theme.value.first.toAbsolute()
                                                                                textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                            }

                                                                        children += Container()
                                                                            .apply {
                                                                                x = Side.POSITIVE.toSide()
                                                                                width = 1.0.toCopy()

                                                                                setPadding(Relative(0.2, true))

                                                                                backgroundCornerRadius =
                                                                                    0.015.toRelative()
                                                                                borderThickness = 0.4.toAbsolute()

                                                                                backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                                borderColor =
                                                                                    { state: Map<String, Any> ->
                                                                                        if (state["hovered"] as Boolean) {
                                                                                            { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                                                        } else {
                                                                                            Color.fromRGB(
                                                                                                10,
                                                                                                10,
                                                                                                10,
                                                                                                150
                                                                                            )
                                                                                        }
                                                                                    }.toDependent()

                                                                                children += Container()
                                                                                    .apply {
                                                                                        width = 0.5.toRelative()
                                                                                        height = 1.0.toCopy()

                                                                                        backgroundImage = "sorus/ui/themes/add.png".toAbsolute()
                                                                                        backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                    }

                                                                                onClick = { state ->
                                                                                    val theme = theme.key.getConstructor().newInstance()
                                                                                    ThemeManager.configuredThemes.add(0, theme)
                                                                                    theme.initialize()
                                                                                    ThemeManager.closeGui()
                                                                                    ThemeManager.openMenuGui("themes")
                                                                                    state["hasInitThemes"] = false
                                                                                }
                                                                            }
                                                                    }
                                                            }

                                                        }

                                                }

                                            }


                                        for (onInit in onInit) {
                                            onInit(Pair(this, HashMap()))
                                        }

                                    })

                                addChild("social", Container()
                                    .apply {
                                        onClose += { state ->
                                            val displayedCategory = state["currentSettingsCategory"] as DisplayedCategory
                                            displayedCategory.onHide()
                                        }

                                        onInit += { state ->
                                            if (state.second["resetSettingsScreen"] == null || state.second["resetSettingsScreen"] as Boolean) {
                                                state.second["currentSettingsCategory"] = SettingManager.mainUICategory
                                            }
                                        }

                                        children += Container()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = 0.3.toRelative()

                                                backgroundCornerRadius = 0.0155.toRelative()
                                                setPadding(0.0125.toRelative())
                                                paddingLeft = 0.0.toAbsolute()

                                                backgroundColor = { this@DefaultTheme.backgroundColor.value }.toDependent()
                                                borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                borderThickness = 0.4.toAbsolute()

                                                onStateUpdate["hasInitGroups"] = { state ->
                                                    state["hasInit"] = false
                                                }

                                                onInit += {
                                                    clear()

                                                    children += Container()
                                                        .apply {
                                                            y = Side.NEGATIVE.toSide()
                                                            height = 0.035.toRelative()
                                                            setPadding(0.05.toRelative())

                                                            children += Text()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()

                                                                    fontRenderer = "sorus/ui/font/Quicksand-Bold.ttf".toAbsolute()
                                                                    scale = 0.0075.toRelative()
                                                                    text = "Groups".toAbsolute()
                                                                    textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                }
                                                        }

                                                    children += TabHolder()
                                                        .apply {
                                                            onUpdate += { state ->
                                                                if (!WebSocketManager.connected) {
                                                                    state["groupsTab"] = "noSocket"
                                                                } else if (SocialManager.currentGroup != null) {
                                                                    state["groupsTab"] = "group"
                                                                } else {
                                                                    state["groupsTab"] = "noGroup"
                                                                }
                                                            }

                                                            storedState += "groupsTab"
                                                            stateId = "groupsTab"

                                                            addChild("noSocket", Container()
                                                                .apply {
                                                                    children += Text()
                                                                        .apply {
                                                                            y = (-0.05).toRelative()

                                                                            scale = 0.006.toRelative()
                                                                            fontRenderer = "sorus/ui/font/Quicksand-Regular.ttf".toAbsolute()
                                                                            text = "Socket Not Connected".toAbsolute()
                                                                            textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                        }
                                                                })

                                                            addChild("noGroup", Container()
                                                                .apply {
                                                                    children += Container()
                                                                        .apply {
                                                                            y = (-0.05).toRelative()
                                                                            width = 0.4.toRelative()
                                                                            height = 0.3.toCopy()

                                                                            backgroundCornerRadius = 0.025.toRelative()
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

                                                                            children += Container()
                                                                                .apply {
                                                                                    x = Side.NEGATIVE.toSide()
                                                                                    width = 1.0.toCopy()
                                                                                    height = 0.5.toRelative()
                                                                                    setPadding(Relative(0.2, true))

                                                                                    backgroundImage = "sorus/ui/groups/invite.png".toAbsolute()
                                                                                    backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                }

                                                                            children += Text()
                                                                                .apply {
                                                                                    setPadding(Relative(0.2, true))

                                                                                    scale = 0.012.toRelative()
                                                                                    fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                                    text = "Create".toAbsolute()
                                                                                    textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                }

                                                                            onClick = { state ->
                                                                                SocialManager.createGroup()
                                                                                state["hasInitGroups"] = false
                                                                            }
                                                                        }
                                                                })

                                                            addChild("group", Container()
                                                                .apply {
                                                                    onInit += {
                                                                        clear()

                                                                        if (SocialManager.currentGroup!!.owner) {
                                                                            children += Container()
                                                                                .apply {
                                                                                    y = Side.NEGATIVE.toSide()
                                                                                    height = 0.06125.toRelative()

                                                                                    paddingLeft = 0.075.toRelative()
                                                                                    paddingRight = 0.075.toRelative()

                                                                                    children += Container()
                                                                                        .apply {
                                                                                            x = Side.NEGATIVE.toSide()
                                                                                            width = 0.475.toRelative()

                                                                                            backgroundCornerRadius = 0.025.toRelative()
                                                                                            backgroundColor = Dependent { state ->
                                                                                                if (state["clicked"] != null && state["clicked"] as Boolean && AdapterManager.adapter.currentServer != null) {
                                                                                                    { this@DefaultTheme.selectedColor.value }.toDependent()
                                                                                                } else {
                                                                                                    { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                                                }
                                                                                            }
                                                                                            borderThickness = 0.4.toAbsolute()
                                                                                            borderColor = Dependent { state ->
                                                                                                if (((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) && AdapterManager.adapter.currentServer != null) {
                                                                                                    { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                                                                } else {
                                                                                                    { this@DefaultTheme.borderColor.value }.toDependent()
                                                                                                }
                                                                                            }

                                                                                            children += Container()
                                                                                                .apply {
                                                                                                    x = Side.NEGATIVE.toSide()
                                                                                                    width = 1.0.toCopy()
                                                                                                    height = 0.5.toRelative()
                                                                                                    setPadding(Relative(0.2, true))

                                                                                                    backgroundImage = "sorus/ui/groups/warp.png".toAbsolute()
                                                                                                    backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                                }

                                                                                            children += Text()
                                                                                                .apply {
                                                                                                    x = Side.NEGATIVE.toSide()
                                                                                                    setPadding(Relative(0.2, true))

                                                                                                    scale = 0.012.toRelative()
                                                                                                    fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                                                    text = "Warp".toAbsolute()
                                                                                                    textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                                }

                                                                                            onClick = {
                                                                                                if (AdapterManager.adapter.currentServer != null) {
                                                                                                    SocialManager.warpGroup()
                                                                                                }
                                                                                            }
                                                                                        }

                                                                                    children += Container()
                                                                                        .apply {
                                                                                            x = Side.POSITIVE.toSide()
                                                                                            width = 0.475.toRelative()

                                                                                            backgroundCornerRadius = 0.025.toRelative()
                                                                                            backgroundColor = Dependent { state ->
                                                                                                if (state["clicked"] != null && state["clicked"] as Boolean) {
                                                                                                    { this@DefaultTheme.selectedColor.value }.toDependent()
                                                                                                } else {
                                                                                                    { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                                                }
                                                                                            }
                                                                                            borderThickness = 0.4.toAbsolute()
                                                                                            borderColor = Dependent { state ->
                                                                                                if (((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean)) {
                                                                                                    { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                                                                } else {
                                                                                                    { this@DefaultTheme.borderColor.value }.toDependent()
                                                                                                }
                                                                                            }

                                                                                            children += Container()
                                                                                                .apply {
                                                                                                    x = Side.NEGATIVE.toSide()
                                                                                                    width = 1.0.toCopy()
                                                                                                    height = 0.5.toRelative()
                                                                                                    setPadding(Relative(0.2, true))

                                                                                                    backgroundImage = "sorus/ui/groups/disband.png".toAbsolute()
                                                                                                    backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                                }

                                                                                            children += Text()
                                                                                                .apply {
                                                                                                    x = Side.NEGATIVE.toSide()
                                                                                                    setPadding(Relative(0.2, true))

                                                                                                    scale = 0.012.toRelative()
                                                                                                    fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                                                    text = "Disband".toAbsolute()
                                                                                                    textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                                }

                                                                                            onClick = {
                                                                                                SocialManager.disbandGroup()
                                                                                                SocialManager.currentGroup = null
                                                                                            }
                                                                                        }
                                                                                }

                                                                            children += Container()
                                                                                .apply {
                                                                                    y = Side.NEGATIVE.toSide()
                                                                                    height = 0.06125.toRelative()
                                                                                    backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                                    borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                                                    backgroundCornerRadius = 0.025.toRelative()
                                                                                    borderThickness = 0.4.toAbsolute()

                                                                                    paddingLeft = 0.075.toRelative()
                                                                                    paddingRight = 0.075.toRelative()
                                                                                    paddingTop = 0.0375.toRelative()
                                                                                    paddingBottom = 0.0375.toRelative()

                                                                                    var message = ""
                                                                                    var selected = false

                                                                                    onStateUpdate["selected"] = { state ->
                                                                                        selected = state["selected"] as Boolean
                                                                                    }

                                                                                    var prevKeyTime = System.currentTimeMillis()

                                                                                    onChar = { state ->
                                                                                        message += state.second.character
                                                                                        prevKeyTime = System.currentTimeMillis()
                                                                                    }

                                                                                    onKey = { state ->
                                                                                        if (state.second.isPressed) {
                                                                                            when (state.second.key) {
                                                                                                Key.BACKSPACE -> message = message.substring(0, message.length - 1)
                                                                                                Key.ENTER -> {
                                                                                                    SocialManager.invite(MojangUtil.getUUID(message))
                                                                                                    message = ""
                                                                                                }
                                                                                            }
                                                                                            prevKeyTime = System.currentTimeMillis()
                                                                                        }
                                                                                    }

                                                                                    children += Container()
                                                                                        .apply {
                                                                                            x = Side.NEGATIVE.toSide()
                                                                                            width = 1.0.toCopy()
                                                                                            height = 0.5.toRelative()

                                                                                            setPadding(Relative(0.2, true))
                                                                                            paddingLeft = Relative(0.3, true)

                                                                                            backgroundImage = "sorus/ui/groups/invite.png".toAbsolute()
                                                                                            backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                        }

                                                                                    children += Text().apply {
                                                                                        x = Side.NEGATIVE.toSide()
                                                                                        y = 0.0.toRelative()

                                                                                        paddingLeft = 0.05.toRelative()

                                                                                        text = {
                                                                                            if (message.isNotEmpty() || selected) { message } else { "Invite..." }
                                                                                        }.toDependent()
                                                                                        fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()

                                                                                        scale = 0.0065.toRelative()
                                                                                    }

                                                                                    children += Container()
                                                                                        .apply {
                                                                                            x = Side.NEGATIVE.toSide()
                                                                                            width = 0.2.toAbsolute()
                                                                                            height = 0.6.toRelative()

                                                                                            backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()

                                                                                            onUpdate += { state ->
                                                                                                state["hidden"] = !selected || ((System.currentTimeMillis() - prevKeyTime) % 1000 > 500)
                                                                                            }
                                                                                        }
                                                                                }

                                                                            children += Container()
                                                                                .apply {
                                                                                    y = Side.NEGATIVE.toSide()
                                                                                    width = 0.8.toRelative()
                                                                                    height = 0.6.toAbsolute()

                                                                                    paddingLeft = 0.05.toRelative()
                                                                                    paddingRight = 0.05.toRelative()
                                                                                    paddingTop = 0.025.toRelative()
                                                                                    paddingBottom = 0.025.toRelative()

                                                                                    backgroundColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                                                }
                                                                        }

                                                                        children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                                                            .apply {
                                                                                paddingLeft = 0.075.toRelative()
                                                                                paddingRight = 0.075.toRelative()
                                                                                paddingTop = 0.0375.toRelative()
                                                                                paddingBottom = 0.0375.toRelative()

                                                                                var cachedMembers = 0

                                                                                onUpdate += { state ->
                                                                                    if (SocialManager.currentGroup!!.members.size != cachedMembers) {
                                                                                        cachedMembers = SocialManager.currentGroup!!.members.size
                                                                                        state["hasInit"] = false
                                                                                    }
                                                                                }

                                                                                onInit += {
                                                                                    clear()

                                                                                    for (member in SocialManager.currentGroup!!.members) {
                                                                                        addChild(Container()
                                                                                            .apply {
                                                                                                height = 0.15.toCopy()

                                                                                                children += Container()
                                                                                                    .apply {
                                                                                                        x = Side.POSITIVE.toSide()

                                                                                                        backgroundCornerRadius = 0.035.toRelative()
                                                                                                        borderThickness = 0.4.toAbsolute()

                                                                                                        backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()

                                                                                                        borderColor = { this@DefaultTheme.borderColor.value }.toDependent()

                                                                                                        Thread {
                                                                                                            AdapterManager.adapter.renderer.createTexture("$member-skin", MojangUtil.getSkin(member).openStream(), false)
                                                                                                        }.start()

                                                                                                        children += Container()
                                                                                                            .apply {
                                                                                                                x = Side.NEGATIVE.toSide()
                                                                                                                width = 1.0.toCopy()
                                                                                                                height = 0.5.toRelative()
                                                                                                                setPadding(Relative(0.25, true))

                                                                                                                children += Container()
                                                                                                                    .apply {
                                                                                                                        x = 0.0.toRelative()
                                                                                                                        y = 0.0.toRelative()
                                                                                                                        width = 1.0.toRelative()
                                                                                                                        height = 1.0.toRelative()

                                                                                                                        backgroundCornerRadius = 0.15.toRelative()
                                                                                                                        backgroundImage = "$member-skin".toAbsolute()
                                                                                                                        backgroundImageBounds = arrayOf(0.125, 0.125, 0.125, 0.125)
                                                                                                                    }

                                                                                                                children += Container()
                                                                                                                    .apply {
                                                                                                                        x = 0.0.toRelative()
                                                                                                                        y = 0.0.toRelative()
                                                                                                                        width = 1.0.toRelative()
                                                                                                                        height = 1.0.toRelative()

                                                                                                                        backgroundCornerRadius = 0.15.toRelative()
                                                                                                                        backgroundImage = "$member-skin".toAbsolute()
                                                                                                                        backgroundImageBounds = arrayOf(0.625, 0.125, 0.125, 0.125)
                                                                                                                    }
                                                                                                            }

                                                                                                        children += Text()
                                                                                                            .apply {
                                                                                                                x = Side.NEGATIVE.toSide()

                                                                                                                scale = 0.0075.toRelative()
                                                                                                                fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()

                                                                                                                var name = ""

                                                                                                                Thread {
                                                                                                                    name = MojangUtil.getUsername(member)
                                                                                                                }.start()

                                                                                                                text = { name }.toDependent()
                                                                                                                textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                                            }

                                                                                                        children += Container()
                                                                                                            .apply {
                                                                                                                x = Side.POSITIVE.toSide()
                                                                                                                width = 1.0.toCopy()
                                                                                                                height = 0.65.toRelative()

                                                                                                                setPadding(Relative(0.175, true))

                                                                                                                backgroundCornerRadius = 0.0175.toRelative()
                                                                                                                borderThickness = 0.4.toAbsolute()

                                                                                                                backgroundColor = Dependent { state ->
                                                                                                                    if (state["clicked"] != null && state["clicked"] as Boolean) {
                                                                                                                        { this@DefaultTheme.selectedColor.value }.toDependent()
                                                                                                                    } else {
                                                                                                                        { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                                                                    }
                                                                                                                }
                                                                                                                borderColor = Dependent { state ->
                                                                                                                    if (((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean)) {
                                                                                                                        { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                                                                                    } else {
                                                                                                                        { this@DefaultTheme.borderColor.value }.toDependent()
                                                                                                                    }
                                                                                                                }

                                                                                                                children += Container()
                                                                                                                    .apply {
                                                                                                                        width = 0.55.toRelative()
                                                                                                                        height = 1.0.toCopy()

                                                                                                                        backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                                                        backgroundImage = "sorus/ui/friends/unfriend.png".toAbsolute()
                                                                                                                    }

                                                                                                                onClick = {
                                                                                                                    SocialManager.removeGroupMember(member)
                                                                                                                }
                                                                                                            }
                                                                                                    }
                                                                                            })

                                                                                        addChild(Container()
                                                                                            .apply {
                                                                                                height = 0.05.toCopy()
                                                                                            })
                                                                                    }
                                                                                }

                                                                                for (onInit in onInit) {
                                                                                    onInit(Pair(this, HashMap()))
                                                                                }

                                                                                onStateUpdate["hasInitProfiles"] = { state ->
                                                                                    if (state["hasInitProfiles"] == false) {
                                                                                        state["hasInitProfiles"] = true
                                                                                        state["hasInit"] = false
                                                                                    }
                                                                                }
                                                                            }
                                                                    }
                                                                })
                                                        }
                                                }

                                                for (onInit in onInit) {
                                                    onInit(Pair(this, HashMap()))
                                                }

                                                storedState += "hasInitGroups"
                                            }

                                        children += Container()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = 0.4.toRelative()

                                                backgroundCornerRadius = 0.0155.toRelative()
                                                setPadding(0.0125.toRelative())

                                                backgroundColor = { this@DefaultTheme.backgroundColor.value }.toDependent()
                                                borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                borderThickness = 0.4.toAbsolute()

                                                children += Container()
                                                    .apply {
                                                        y = Side.NEGATIVE.toSide()
                                                        height = 0.02625.toRelative()
                                                        setPadding(0.05.toRelative())

                                                        children += Text()
                                                            .apply {
                                                                x = Side.NEGATIVE.toSide()

                                                                fontRenderer = "sorus/ui/font/Quicksand-Bold.ttf".toAbsolute()
                                                                scale = 0.005625.toRelative()
                                                                text = "Friends".toAbsolute()
                                                                textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                            }
                                                    }

                                                children += TabHolder()
                                                    .apply {
                                                        onUpdate += { state ->
                                                            if (!WebSocketManager.connected) {
                                                                state["groupsTab"] = "noSocket"
                                                            } else {
                                                                state["groupsTab"] = "friendsList"
                                                            }
                                                        }

                                                        storedState += "groupsTab"
                                                        stateId = "groupsTab"

                                                        addChild("noSocket", Container()
                                                            .apply {
                                                                children += Text()
                                                                    .apply {
                                                                        y = (-0.05).toRelative()

                                                                        scale = 0.006.toRelative()
                                                                        fontRenderer = "sorus/ui/font/Quicksand-Regular.ttf".toAbsolute()
                                                                        text = "Socket Not Connected".toAbsolute()
                                                                        textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                    }
                                                            })

                                                        addChild("noGroup", Container()
                                                            .apply {
                                                                children += Container()
                                                                    .apply {
                                                                        y = (-0.05).toRelative()
                                                                        width = 0.4.toRelative()
                                                                        height = 0.3.toCopy()

                                                                        backgroundCornerRadius = 0.025.toRelative()
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

                                                                        children += Container()
                                                                            .apply {
                                                                                x = Side.NEGATIVE.toSide()
                                                                                width = 1.0.toCopy()
                                                                                height = 0.5.toRelative()
                                                                                setPadding(Relative(0.2, true))

                                                                                backgroundImage = "sorus/ui/groups/invite.png".toAbsolute()
                                                                                backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                            }

                                                                        children += Text()
                                                                            .apply {
                                                                                setPadding(Relative(0.2, true))

                                                                                scale = 0.012.toRelative()
                                                                                fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                                text = "Create".toAbsolute()
                                                                                textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                            }

                                                                        onClick = { state ->
                                                                            SocialManager.createGroup()
                                                                            state["hasInitGroups"] = false
                                                                        }
                                                                    }
                                                            })

                                                        addChild("friendsList", Container()
                                                            .apply {
                                                                onInit += {
                                                                    clear()

                                                                    children += Container()
                                                                        .apply {
                                                                            y = Side.NEGATIVE.toSide()
                                                                            height = 0.08.toRelative()
                                                                            backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                            borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                                            backgroundCornerRadius = 0.025.toRelative()
                                                                            borderThickness = 0.4.toAbsolute()

                                                                            paddingLeft = 0.075.toRelative()
                                                                            paddingRight = 0.075.toRelative()
                                                                            paddingTop = 0.0375.toRelative()
                                                                            paddingBottom = 0.0375.toRelative()

                                                                            var message = ""
                                                                            var selected = false

                                                                            onStateUpdate["selected"] = { state ->
                                                                                selected = state["selected"] as Boolean
                                                                            }

                                                                            var prevKeyTime = System.currentTimeMillis()

                                                                            onChar = { state ->
                                                                                message += state.second.character
                                                                                prevKeyTime = System.currentTimeMillis()
                                                                            }

                                                                            onKey = { state ->
                                                                                if (state.second.isPressed) {
                                                                                    when (state.second.key) {
                                                                                        Key.BACKSPACE -> message = message.substring(0, message.length - 1)
                                                                                        Key.ENTER -> {
                                                                                            SocialManager.sendFriend(MojangUtil.getUUID(message))
                                                                                            message = ""
                                                                                        }
                                                                                    }
                                                                                    prevKeyTime = System.currentTimeMillis()
                                                                                }
                                                                            }

                                                                            children += Container()
                                                                                .apply {
                                                                                    x = Side.NEGATIVE.toSide()
                                                                                    width = 1.0.toCopy()
                                                                                    height = 0.5.toRelative()
                                                                                    setPadding(Relative(0.2, true))
                                                                                    paddingLeft = Relative(0.3, true)

                                                                                    backgroundImage = "sorus/ui/friends/friend.png".toAbsolute()
                                                                                    backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                }

                                                                            children += Text().apply {
                                                                                x = Side.NEGATIVE.toSide()
                                                                                y = 0.0.toRelative()

                                                                                paddingLeft = 0.05.toRelative()

                                                                                text = {
                                                                                    if (message.isNotEmpty() || selected) { message } else { "Send Friend Request..." }
                                                                                }.toDependent()
                                                                                fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()

                                                                                scale = 0.005.toRelative()
                                                                            }

                                                                            children += Container()
                                                                                .apply {
                                                                                    x = Side.NEGATIVE.toSide()
                                                                                    width = 0.2.toAbsolute()
                                                                                    height = 0.6.toRelative()

                                                                                    backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()

                                                                                    onUpdate += { state ->
                                                                                        state["hidden"] = !selected || ((System.currentTimeMillis() - prevKeyTime) % 1000 > 500)
                                                                                    }
                                                                                }
                                                                        }

                                                                    children += Container()
                                                                        .apply {
                                                                            y = Side.NEGATIVE.toSide()
                                                                            width = 0.8.toRelative()
                                                                            height = 0.6.toAbsolute()

                                                                            paddingLeft = 0.05.toRelative()
                                                                            paddingRight = 0.05.toRelative()
                                                                            paddingTop = 0.025.toRelative()
                                                                            paddingBottom = 0.025.toRelative()

                                                                            backgroundColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                                                        }

                                                                    children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                                                        .apply {
                                                                            paddingLeft = 0.075.toRelative()
                                                                            paddingRight = 0.075.toRelative()
                                                                            paddingTop = 0.0375.toRelative()
                                                                            paddingBottom = 0.0375.toRelative()

                                                                            var cachedSize = 0

                                                                            onUpdate += { state ->
                                                                                if (SocialManager.friends.size != cachedSize) {
                                                                                    cachedSize = SocialManager.friends.size
                                                                                    state["hasInit"] = false
                                                                                }
                                                                            }

                                                                            onInit += {
                                                                                clear()

                                                                                for (friend in SocialManager.friends) {
                                                                                    addChild(Container()
                                                                                        .apply {
                                                                                            height = { if (friend.second.second == "offline") { 0.12 } else { 0.20 }.toCopy() }.toDependent()

                                                                                            children += List(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                                                                                .apply {
                                                                                                    backgroundCornerRadius = 0.035.toRelative()
                                                                                                    borderThickness = 0.4.toAbsolute()

                                                                                                    backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()

                                                                                                    borderColor = { this@DefaultTheme.borderColor.value }.toDependent()

                                                                                                    children += Container()
                                                                                                        .apply {
                                                                                                            height = { if (friend.second.second == "offline") { Flexible() } else { 0.6.toRelative() } }.toDependent()
                                                                                                            setPadding(0.001.toRelative())

                                                                                                            Thread {
                                                                                                                AdapterManager.adapter.renderer.createTexture("${friend.first}-skin", MojangUtil.getSkin(friend.first).openStream(), false)
                                                                                                            }.start()

                                                                                                            children += Container()
                                                                                                                .apply {
                                                                                                                    x = Side.NEGATIVE.toSide()
                                                                                                                    width = 1.0.toCopy()
                                                                                                                    height = 0.5.toRelative()
                                                                                                                    setPadding(Relative(0.25, true))

                                                                                                                    children += Container()
                                                                                                                        .apply {
                                                                                                                            x = 0.0.toRelative()
                                                                                                                            y = 0.0.toRelative()
                                                                                                                            width = 1.0.toRelative()
                                                                                                                            height = 1.0.toRelative()

                                                                                                                            backgroundCornerRadius = 0.15.toRelative()
                                                                                                                            backgroundImage = "${friend.first}-skin".toAbsolute()
                                                                                                                            backgroundImageBounds = arrayOf(0.125, 0.125, 0.125, 0.125)
                                                                                                                        }

                                                                                                                    children += Container()
                                                                                                                        .apply {
                                                                                                                            x = 0.0.toRelative()
                                                                                                                            y = 0.0.toRelative()
                                                                                                                            width = 1.0.toRelative()
                                                                                                                            height = 1.0.toRelative()

                                                                                                                            backgroundCornerRadius = 0.15.toRelative()
                                                                                                                            backgroundImage = "${friend.first}-skin".toAbsolute()
                                                                                                                            backgroundImageBounds = arrayOf(0.625, 0.125, 0.125, 0.125)

                                                                                                                            borderThickness = 0.055.toRelative()
                                                                                                                            borderColor = Dependent { if (friend.second.second == "offline") { Color.fromRGB(0, 0, 0, 0).toAbsolute() } else { Color.fromRGB(45, 175, 60, 255).toAbsolute() } }
                                                                                                                        }
                                                                                                                }

                                                                                                            children += Text()
                                                                                                                .apply {
                                                                                                                    x = Side.NEGATIVE.toSide()

                                                                                                                    scale = 0.006.toRelative()
                                                                                                                    fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()

                                                                                                                    var name = ""

                                                                                                                    Thread {
                                                                                                                        name = MojangUtil.getUsername(friend.first)
                                                                                                                    }.start()

                                                                                                                    text = { name }.toDependent()
                                                                                                                    textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                                                }

                                                                                                            children += Container()
                                                                                                                .apply {
                                                                                                                    x = Side.POSITIVE.toSide()
                                                                                                                    width = 1.0.toCopy()
                                                                                                                    height = 0.65.toRelative()

                                                                                                                    setPadding(Relative(0.175, true))

                                                                                                                    backgroundCornerRadius = 0.0175.toRelative()
                                                                                                                    borderThickness = 0.4.toAbsolute()

                                                                                                                    backgroundColor = Dependent { state ->
                                                                                                                        if (state["clicked"] != null && state["clicked"] as Boolean && friend.second.second != "offline" && SocialManager.currentGroup != null) {
                                                                                                                            { this@DefaultTheme.selectedColor.value }.toDependent()
                                                                                                                        } else {
                                                                                                                            { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                                                                        }
                                                                                                                    }
                                                                                                                    borderColor = Dependent { state ->
                                                                                                                        if (((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) && friend.second.second != "offline" && SocialManager.currentGroup != null) {
                                                                                                                            { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                                                                                        } else {
                                                                                                                            { this@DefaultTheme.borderColor.value }.toDependent()
                                                                                                                        }
                                                                                                                    }

                                                                                                                    children += Container()
                                                                                                                        .apply {
                                                                                                                            width = 0.55.toRelative()
                                                                                                                            height = 1.0.toCopy()

                                                                                                                            backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                                                            backgroundImage = "sorus/ui/groups/invite.png".toAbsolute()
                                                                                                                        }

                                                                                                                    onClick = {
                                                                                                                        if (friend.second.second != "offline" && SocialManager.currentGroup != null) {
                                                                                                                            SocialManager.invite(friend.first)
                                                                                                                        }
                                                                                                                    }
                                                                                                                }

                                                                                                            children += Container()
                                                                                                                .apply {
                                                                                                                    x = Side.POSITIVE.toSide()
                                                                                                                    width = 1.0.toCopy()
                                                                                                                    height = 0.65.toRelative()

                                                                                                                    setPadding(Relative(0.175, true))

                                                                                                                    backgroundCornerRadius = 0.0175.toRelative()
                                                                                                                    borderThickness = 0.4.toAbsolute()

                                                                                                                    backgroundColor = Dependent { state ->
                                                                                                                        if (state["clicked"] != null && state["clicked"] as Boolean) {
                                                                                                                            { this@DefaultTheme.selectedColor.value }.toDependent()
                                                                                                                        } else {
                                                                                                                            { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                                                                                        }
                                                                                                                    }
                                                                                                                    borderColor = Dependent { state ->
                                                                                                                        if (((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean)) {
                                                                                                                            { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                                                                                        } else {
                                                                                                                            { this@DefaultTheme.borderColor.value }.toDependent()
                                                                                                                        }
                                                                                                                    }

                                                                                                                    children += Container()
                                                                                                                        .apply {
                                                                                                                            width = 0.55.toRelative()
                                                                                                                            height = 1.0.toCopy()

                                                                                                                            backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                                                            backgroundImage = "sorus/ui/friends/unfriend.png".toAbsolute()
                                                                                                                        }

                                                                                                                    onClick = {
                                                                                                                        SocialManager.unfriend(friend.first)
                                                                                                                    }
                                                                                                                }
                                                                                                        }

                                                                                                    children += Container()
                                                                                                        .apply {
                                                                                                            height = { if(friend.second.second == "offline") { 0.0.toAbsolute() } else { 0.4.toRelative() } }.toDependent()

                                                                                                            children += Text()
                                                                                                                .apply {
                                                                                                                    x = Side.NEGATIVE.toSide()

                                                                                                                    paddingLeft = 0.125.toRelative()
                                                                                                                    paddingBottom = Relative(0.4, true)

                                                                                                                    scale = 0.0055.toRelative()
                                                                                                                    fontRenderer = "sorus/ui/font/Quicksand-Regular.ttf".toAbsolute()
                                                                                                                    text = {
                                                                                                                        if (friend.second.second == "offline") {
                                                                                                                            "Offline"
                                                                                                                        } else if (!friend.second.second.isEmpty()) {
                                                                                                                            "Playing ${friend.second.second} on ${friend.second.first}"
                                                                                                                        } else {
                                                                                                                            "Playing on ${friend.second.first}"
                                                                                                                        }
                                                                                                                    }.toDependent()
                                                                                                                    textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                                                }

                                                                                                            onUpdate += { state ->
                                                                                                                state["hidden"] = friend.second.second == "offline"
                                                                                                            }
                                                                                                        }
                                                                                                }
                                                                                        })

                                                                                    addChild(Container()
                                                                                        .apply {
                                                                                            height = 0.05.toCopy()
                                                                                        })
                                                                                }
                                                                            }

                                                                            for (onInit in onInit) {
                                                                                onInit(Pair(this, HashMap()))
                                                                            }

                                                                            onStateUpdate["hasInitProfiles"] = { state ->
                                                                                if (state["hasInitProfiles"] == false) {
                                                                                    state["hasInitProfiles"] = true
                                                                                    state["hasInit"] = false
                                                                                }
                                                                            }
                                                                        }
                                                                }
                                                            })
                                                    }

                                                storedState += "hasInitGroups"
                                            }
                                    })

                                addChild("custom", Container()
                                    .apply {
                                        onUpdate += { state ->
                                            if (state["customContainer"] != state["prevCustomContainer"]) {
                                                state["prevCustomContainer"] = state["customContainer"]!!

                                                val container = state["customContainer"] as Container

                                                clear()

                                                children += container
                                            }
                                        }
                                    })
                            }

                        storedState += "customContainer"
                        storedState += "resetSettingsScreen"
                        storedState += "prevCustomContainer"
                        storedState += "currentSettingsCategory"
                        storedState += "keepState"

                        storedState += "tab"
                    }
            }

        searchGui = Container()
            .apply {
                children += Container()
                    .apply {
                        var searchResults: List<UserInterface.SearchResult> = ArrayList()

                        width = 0.25.toRelative()
                        height = {
                            (0.12 + if (searchResults.isNotEmpty()) { 0.032 } else { 0.0 } + (searchResults.size * 0.138)).toCopy()
                        }.toDependent()

                        backgroundCornerRadius = 0.01.toRelative()

                        borderThickness = 0.4.toAbsolute()
                        backgroundColor = { this@DefaultTheme.backgroundColor.value }.toDependent()
                        borderColor = { this@DefaultTheme.borderColor.value }.toDependent()

                        storedState += "searchParameter"
                        storedState += "searchResults"
                        storedState += "selectedResult"

                        onInit += { state ->
                            state.second["searchParameter"] = ""
                        }

                        val results = UserInterface.searchResults

                        onStateUpdate["searchParameter"] = { state ->
                            searchResults = UserInterface.search(state["searchParameter"] as String, results, 2.0, 5)
                        }

                        children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                            .apply {
                                children += Container()
                                    .apply {
                                        height = 0.12.toCopy()

                                        selectedByDefault = true

                                        children += Container()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = 1.0.toCopy()
                                                setPadding(0.025.toRelative())

                                                backgroundImage = "sorus/ui/search/magnifying_glass.png".toAbsolute()
                                                backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                            }

                                        children += Text()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                paddingLeft = 0.05.toRelative()

                                                fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                scale = 0.0075.toRelative()
                                                text = { state: Map<String, Any> ->
                                                    if (state["searchParameter"] == null) {
                                                        ""
                                                    } else {
                                                        state["searchParameter"] as String
                                                    }
                                                }.toDependent()
                                                textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                            }

                                        var prevKeyTime = System.currentTimeMillis()

                                        children += Container()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = 0.4.toAbsolute()
                                                height = 0.6.toRelative()

                                                backgroundColor = Color.WHITE.toAbsolute()

                                                onUpdate += { state ->
                                                    state["hidden"] = (System.currentTimeMillis() - prevKeyTime) % 1000 > 500
                                                }
                                            }

                                        onKey = onKey@{ state ->
                                            if (!state.second.isPressed) return@onKey

                                            var parameter = if (state.first["searchParameter"] == null) { "" } else { state.first["searchParameter"] as String }

                                            if (state.second.key == Key.BACKSPACE && parameter.isNotEmpty()) {
                                                parameter = parameter.substring(0, parameter.length - 1)
                                            } else if (state.second.key == Key.SPACE) {
                                                parameter += " "
                                            } else if (state.second.key == Key.ARROW_DOWN) {
                                                var newSelectedResult = state.first.getOrDefault("selectedResult", 0) as Int
                                                newSelectedResult++
                                                if (newSelectedResult > searchResults.size - 1) {
                                                    newSelectedResult = 0
                                                }
                                                state.first["selectedResult"] = newSelectedResult
                                            } else if (state.second.key == Key.ARROW_UP) {
                                                var newSelectedResult = state.first.getOrDefault("selectedResult", 0) as Int
                                                newSelectedResult--
                                                if (newSelectedResult < 0) {
                                                    newSelectedResult = searchResults.size - 1
                                                }
                                                state.first["selectedResult"] = newSelectedResult
                                            } else if (state.second.key == Key.ENTER) {
                                                val result = searchResults.getOrNull(state.first.getOrDefault("selectedResult", 0) as Int)
                                                if (result != null) {
                                                    ContainerRenderer.close(searchGui)
                                                    result.onSelect()
                                                }
                                            }

                                            prevKeyTime = System.currentTimeMillis()

                                            state.first["searchParameter"] = parameter
                                        }

                                        onChar = { state ->
                                            var parameter = if (state.first["searchParameter"] == null) { "" } else { state.first["searchParameter"] as String }
                                            parameter += state.second.character

                                            prevKeyTime = System.currentTimeMillis()
                                            state.first["searchParameter"] = parameter
                                        }
                                    }

                                children += Container()
                                    .apply {
                                        width = 0.9.toRelative()
                                        height = 0.6.toAbsolute()

                                        backgroundColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                    }

                                onStateUpdate["searchParameter"] = {
                                    clearAfter(2)

                                    for ((i, result) in searchResults.withIndex()) {
                                        children += Container()
                                            .apply {
                                                height = 0.12.toCopy()

                                                setPadding(0.025.toRelative())

                                                backgroundCornerRadius = 0.025.toRelative()

                                                backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()
                                                borderThickness = 0.4.toAbsolute()
                                                borderColor = { state: Map<String, Any> ->
                                                    if (state.getOrDefault("selectedResult", 0) == i) {
                                                        { this@DefaultTheme.selectedBorderColor.value }.toDependent()
                                                    } else {
                                                        { this@DefaultTheme.borderColor.value }.toDependent()
                                                    }
                                                }.toDependent()

                                                children += Container()
                                                    .apply {
                                                        x = Side.NEGATIVE.toSide()
                                                        width = 1.0.toCopy()
                                                        setPadding(0.025.toRelative())

                                                        backgroundImage = result.displayImage.toAbsolute()
                                                        backgroundCornerRadius = 0.0175.toRelative()
                                                    }

                                                children += Text()
                                                    .apply {
                                                        x = Side.NEGATIVE.toSide()
                                                        paddingLeft = 0.04.toRelative()

                                                        fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                                        scale = 0.006.toRelative()
                                                        text = result.displayName.toAbsolute()
                                                        textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                    }
                                            }
                                    }
                                }
                            }
                    }
            }

        colorPickerGui = Container()
            .apply {
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

                        children += List(com.github.sorusclient.client.ui.framework.List.HORIZONTAL)
                            .apply {
                                x = Side.POSITIVE.toSide()
                                width = Relative(1.925, true)

                                paddingLeft = Relative(0.2, true)

                                backgroundColor = { this@DefaultTheme.midgroundColor.value }.toDependent()
                                borderThickness = 0.4.toAbsolute()
                                borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
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

                                            //setting.setting.overriden = true
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

                                            //setting.setting.overriden = true
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
                                        backgroundColor = { this@DefaultTheme.backgroundColor.value }.toDependent()
                                        borderColor = { state: Map<String, Any> ->
                                            if (state["hovered"] as Boolean) {
                                                this@DefaultTheme.selectedBorderColor.value
                                            } else {
                                                this@DefaultTheme.borderColor.value
                                            }
                                        }.toDependent()

                                        onClick = {
                                            ContainerRenderer.close(colorPickerGui)
                                        }

                                        children += Container()
                                            .apply {
                                                width = 0.5.toRelative()
                                                height = 1.0.toCopy()

                                                backgroundImage = "sorus/ui/settings/x.png".toAbsolute()
                                                backgroundColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                            }
                                    }
                            }
                    }
            }

        notificationsUi = Container()
            .apply {
                children += List(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                    .apply {
                        x = Side.POSITIVE.toSide()
                        width = 0.2.toRelative()
                        setPadding(0.005.toRelative())

                        val renderedNotifications = HashMap<Notification, Container>()

                        onUpdate += {
                            for (notification in NotificationManager.notifications) {
                                if (!renderedNotifications.containsKey(notification)) {
                                    val container = Container()
                                        .apply {
                                            val contentSplit = ArrayList<String>()
                                            var currentLine = ""
                                            for (word in notification.content.split(" ")) {
                                                if (AdapterManager.adapter.renderer.getTextWidth("sorus/ui/font/Quicksand-Medium.ttf", "$currentLine$word") > 115) {
                                                    contentSplit.add(currentLine)
                                                    currentLine = "$word "
                                                } else {
                                                    currentLine += "$word "
                                                }
                                            }
                                            contentSplit.add(currentLine)

                                            contentSplit.removeIf { it.isEmpty() }

                                            val height2 = max(2.2, 1.0 + contentSplit.size * 0.6 + if (notification.interactions.isNotEmpty()) { 1.25 } else { 0.0 })
                                            height = {
                                                (0.12 * height2).toCopy()
                                            }.toDependent()

                                            backgroundColor = { this@DefaultTheme.backgroundColor.value }.toDependent()
                                            borderColor = { this@DefaultTheme.borderColor.value }.toDependent()
                                            borderThickness = 0.4.toAbsolute()
                                            backgroundCornerRadius = 0.025.toRelative()

                                            setPadding(0.01.toRelative())

                                            children += Container()
                                                .apply {
                                                    x = Side.NEGATIVE.toSide()
                                                    width = 0.275.toRelative()

                                                    children += Container()
                                                        .apply {
                                                            width = 0.7.toRelative()
                                                            height = 1.0.toCopy()

                                                            for (icon in notification.icons) {
                                                                children += Container()
                                                                    .apply {
                                                                        x = 0.0.toRelative()
                                                                        y = 0.0.toRelative()

                                                                        backgroundImage = icon.icon.toAbsolute()
                                                                        backgroundImageBounds = icon.iconBounds
                                                                        backgroundCornerRadius = 0.1.toRelative()
                                                                    }
                                                            }

                                                            if (notification.subIcon != null) {
                                                                children += Container()
                                                                    .apply {
                                                                        x = 0.475.toRelative()
                                                                        y = 0.475.toRelative()
                                                                        width = 0.325.toRelative()
                                                                        height = 0.325.toRelative()

                                                                        backgroundImage = notification.subIcon!!.icon.toAbsolute()
                                                                        backgroundImageBounds = notification.subIcon!!.iconBounds
                                                                        backgroundCornerRadius = 0.1625.toRelative()
                                                                    }
                                                            }
                                                        }
                                                }

                                            children += List(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                                .apply {
                                                    x = Side.POSITIVE.toSide()

                                                    setPadding(0.02.toRelative())

                                                    children += Container()
                                                        .apply {
                                                            height = (1.0 / height2).toRelative()

                                                            children += Text()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()

                                                                    fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                    scale = 0.01.toRelative()
                                                                    text = notification.title.toAbsolute()

                                                                    textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                }
                                                        }

                                                    for (content in contentSplit) {
                                                        children += Container()
                                                            .apply {
                                                                height = (0.6 / height2).toRelative()

                                                                //backgroundColor = Color.fromRGB(0, 255, 0, 255).toAbsolute()
                                                                //setPadding(0.001.toAbsolute())

                                                                children += Text()
                                                                    .apply {
                                                                        x = Side.NEGATIVE.toSide()

                                                                        setPadding(0.001.toRelative())

                                                                        fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                                                        scale = 0.0085.toRelative()
                                                                        text = content.toAbsolute()

                                                                        textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                    }
                                                            }
                                                    }

                                                    children += List(com.github.sorusclient.client.ui.framework.List.HORIZONTAL)
                                                        .apply {
                                                            height = (1.25 / height2).toRelative()

                                                            //setPadding(0.001.toAbsolute())

                                                            for (interaction in notification.interactions) {
                                                                when (interaction) {
                                                                    is Interaction.Button -> {
                                                                        children += Container()
                                                                            .apply {
                                                                                width = 2.75.toCopy()

                                                                                paddingTop = 0.02.toRelative()
                                                                                paddingLeft = 0.02.toRelative()
                                                                                paddingRight = 0.02.toRelative()

                                                                                backgroundColor = { state: Map<String, Any> ->
                                                                                    if (state["clicked"] as Boolean) {
                                                                                        this@DefaultTheme.selectedColor.value
                                                                                    } else {
                                                                                        this@DefaultTheme.midgroundColor.value
                                                                                    }
                                                                                }.toDependent()
                                                                                borderColor = { state: Map<String, Any> ->
                                                                                    if (state["hovered"] as Boolean) {
                                                                                        this@DefaultTheme.selectedBorderColor.value
                                                                                    } else {
                                                                                        this@DefaultTheme.borderColor.value
                                                                                    }
                                                                                }.toDependent()
                                                                                borderThickness = 0.4.toAbsolute()
                                                                                backgroundCornerRadius = 0.025.toRelative()

                                                                                children += Text()
                                                                                    .apply {
                                                                                        fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                                                                        scale = Relative(0.05, true)
                                                                                        text = interaction.text.toAbsolute()

                                                                                        textColor = { this@DefaultTheme.elementColor.value }.toDependent()
                                                                                    }

                                                                                onClick = {
                                                                                    if (interaction.onClick != null) {
                                                                                        interaction.onClick!!()
                                                                                    }
                                                                                    if (interaction.closeOnInteract) {
                                                                                        notification.close()
                                                                                    }
                                                                                }
                                                                            }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                }
                                        }

                                    children += container
                                    renderedNotifications[notification] = container
                                }
                            }

                            for ((notification, container) in HashMap(renderedNotifications).entries) {
                                if (!NotificationManager.notifications.contains(notification)) {
                                    renderedNotifications.remove(notification)
                                    children.remove(container)
                                }
                            }
                        }
                    }
            }

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
                if (arguments.isNotEmpty()) {
                    when (arguments[0]) {
                        is String -> {
                            mainGui.apply {
                                children[0].apply {
                                    runtime.setState("tab", arguments[0])
                                }
                            }
                        }
                        is DisplayedCategory -> {
                            mainGui.apply {
                                children[0].apply {
                                    runtime.setState("tab", "settings")
                                    runtime.setState("currentSettingsCategory", arguments[0] as DisplayedCategory)
                                    runtime.setState("resetSettingsScreen", false)
                                    (arguments[0] as DisplayedCategory).onShow()
                                }
                            }
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