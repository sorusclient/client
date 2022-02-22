package com.github.sorusclient.client.ui

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.ScreenType
import com.github.sorusclient.client.adapter.event.InitializeEvent
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.adapter.event.TickEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.*
import com.github.sorusclient.client.setting.display.Displayed
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting
import com.github.sorusclient.client.ui.framework.*
import com.github.sorusclient.client.ui.framework.constraint.*
import com.github.sorusclient.client.ui.framework.constraint.Dependent
import com.github.sorusclient.client.util.AssetUtil
import com.github.sorusclient.client.util.Color
import org.json.JSONObject
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.network.ServerAddress
import java.lang.reflect.InvocationTargetException
import java.nio.charset.Charset
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.set
import kotlin.math.*

object UserInterface {

    private lateinit var mainGui: Container
    private lateinit var searchGui: Container
    private val guiOpened = AtomicBoolean(false)

    fun initialize() {
        val eventManager = EventManager
        val adapter = AdapterManager.getAdapter()

        eventManager.register { event: KeyEvent ->
            if (!event.isRepeat) {
                if (event.key === Key.SHIFT_RIGHT && event.isPressed && adapter.openScreen === ScreenType.IN_GAME) {
                    adapter.openScreen(ScreenType.DUMMY)

                    mainGui
                        .apply {
                            children[0].runtime.apply {
                                setState("tab", "home")
                            }
                        }

                    ContainerRenderer.open(mainGui)
                    AdapterManager.getAdapter().renderer.loadBlur()
                } else if (event.key === Key.ESCAPE && event.isPressed && adapter.openScreen === ScreenType.DUMMY) {
                    adapter.openScreen(ScreenType.IN_GAME)
                    guiOpened.set(false)
                    ContainerRenderer.close()
                    AdapterManager.getAdapter().renderer.unloadBlur()
                }
            }
        }


        eventManager.register { event: KeyEvent ->
            if (event.isPressed && !event.isRepeat && event.key === Key.U) {
                initializeUserInterface()
            }
        }

        eventManager.register { _: InitializeEvent ->
            initializeUserInterface()
        }

        eventManager.register { event: KeyEvent ->
            if (event.isPressed && event.key == Key.ALT_LEFT) {
                adapter.openScreen(ScreenType.DUMMY)
                ContainerRenderer.open(searchGui)
                AdapterManager.getAdapter().renderer.loadBlur()
            }
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
                                width = 0.05.toRelative()
                            }

                        children += Text()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                text = setting.displayName.toAbsolute()
                                scale = 0.0025.toRelative()
                            }

                        children += Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 0.05.toRelative()
                            }

                        children += Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 2.0.toCopy()
                                height = 0.6.toRelative()
                                setPadding(Relative(0.2, true))

                                backgroundColor = Dependent {
                                    val toggled = setting.setting.value
                                    if (toggled) {
                                        Color.fromRGB(60, 75, 250, 65)
                                    } else {
                                        Color.fromRGB(0, 0, 0, 65)
                                    }
                                }
                                borderThickness = 0.4.toAbsolute()
                                borderColor = Dependent { state ->
                                    val toggled = setting.setting.value
                                    if ((state["hovered"] as Boolean && !setting.setting.isForcedValue) || toggled) {
                                        Color.fromRGB(60, 75, 250, 255)
                                    } else {
                                        Color.fromRGB(0, 0, 0, 100)
                                    }
                                }
                                backgroundCornerRadius = 0.01.toRelative()

                                onClick = { state ->
                                    if (!setting.setting.isForcedValue) {
                                        val toggled = !(state["toggled"] as Boolean)
                                        state["toggled"] = toggled
                                        setting.setting.setValueRaw(toggled)

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

                        storedState += "toggled"
                        runtime.setState("toggled", setting.setting.value)
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
                                width = 0.05.toRelative()
                            }

                        children += Text()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                text = setting.displayName.toAbsolute()
                                scale = 0.0025.toRelative()
                            }

                        children += Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 0.05.toRelative()
                            }

                        children += Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 5.0.toCopy()
                                height = 0.6.toRelative()
                                setPadding(Relative(0.2, true))

                                backgroundColor = Color.fromRGB(0, 0, 0, 65).toAbsolute()

                                borderThickness = 0.4.toAbsolute()
                                borderColor = Dependent { state ->
                                    if (state["hovered"] as Boolean || state["interacted"] as Boolean) {
                                        Color.fromRGB(60, 75, 250, 255)
                                    } else {
                                        Color.fromRGB(0, 0, 0, 100)
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
                                width = 0.05.toRelative()
                            }

                        children += Text()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                text = setting.displayName.toAbsolute()
                                scale = 0.0025.toRelative()
                            }

                        children += Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 0.05.toRelative()
                            }

                        children += Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 5.0.toCopy()
                                height = 0.6.toRelative()
                                setPadding(Relative(0.2, true))

                                backgroundColor = Dependent { state ->
                                    if (state["clicked"] != null && state["clicked"] as Boolean) {
                                        Color.fromRGB(60, 75, 250, 65)
                                    } else {
                                        Color.fromRGB(0, 0, 0, 65)
                                    }
                                }
                                borderThickness = 0.4.toAbsolute()
                                borderColor = Dependent { state ->
                                    if ((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) {
                                        Color.fromRGB(60, 75, 250, 255)
                                    } else {
                                        Color.fromRGB(0, 0, 0, 100)
                                    }
                                }
                                backgroundCornerRadius = 0.01.toRelative()

                                onKey = { state ->
                                    state.first["value"] = state.second
                                    setting.setting.setValueRaw(state.second)
                                    state.first["selected"] = false
                                    setting.setting.overriden = true
                                }

                                children += Text()
                                    .apply {
                                        fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                        text = { state: Map<String, Any> ->
                                            if (state["selected"] as Boolean) {
                                                "..."
                                            } else {
                                                val key = state["value"] as Key?
                                                key.toString()
                                            }
                                        }.toDependent()
                                        scale = 0.01.toRelative()
                                    }
                            }

                        onStateUpdate["selected"] = { state ->
                            if (state["selected"] != null) {
                                state["selected2"] = state["selected"] as Any
                            }
                        }

                        storedState += "value"
                        storedState += "selected2"
                        runtime.setState("value", setting.setting.value)
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
                                width = 0.05.toRelative()
                            }

                        children += Text()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                text = setting.displayName.toAbsolute()
                                scale = 0.0025.toRelative()
                            }

                        children += Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 0.05.toRelative()
                            }

                        children += Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 5.0.toCopy()
                                height = 0.6.toRelative()
                                setPadding(Relative(0.2, true))

                                backgroundColor = Dependent { state ->
                                    if (state["clicked"] != null && state["clicked"] as Boolean) {
                                        Color.fromRGB(60, 75, 250, 65)
                                    } else {
                                        Color.fromRGB(0, 0, 0, 65)
                                    }
                                }
                                borderThickness = 0.4.toAbsolute()
                                borderColor = Dependent { state ->
                                    if ((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) {
                                        Color.fromRGB(60, 75, 250, 255)
                                    } else {
                                        Color.fromRGB(0, 0, 0, 100)
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
                                            val newValue = 0.coerceAtLeast(state["clickThroughValue"] as Int - 1)
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
                                        textColor = Color.WHITE.toAbsolute()
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
            /*is ClickThrough -> {
                return Container()
                        .apply {
                            height = 15.0.toAbsolute()
                            onUpdate += { state ->
                                state["hidden"] = false
                            }

                            children += Container()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.05.toRelative()
                                    }

                            children += Text()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                        text = setting.displayName.toAbsolute()
                                        scale = 0.0025.toRelative()
                                    }

                            children += Container()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.05.toRelative()
                                    }

                            children += TabHolder()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        width = 5.0.toCopy()
                                        height = 0.6.toRelative()
                                        setPadding(Relative(0.2, true)

                                        addChild("view", Container()
                                                .apply {
                                                    backgroundColor = Color.fromRGB(255, 0, 0, 255).toAbsolute()
                                                })

                                        addChild("edit", Container()
                                                .apply {
                                                    val values = (setting.setting.type.getDeclaredMethod("values").invoke(null) as Array<*>)

                                                    children += Container()
                                                            .apply {
                                                                y = ((values.size).toDouble() / 2).toRelative()
                                                                width = 1.0.toRelative()

                                                                height = (values.size + 1).toDouble().toRelative()

                                                                backgroundColor = Color.fromRGB(0, 255, 0, 255).toAbsolute()
                                                            }

                                                    for (value in values) {
                                                        val value = value as Enum<*>

                                                        children += Text()
                                                                .apply {
                                                                    x = 0.0.toRelative()
                                                                    y = (1.0 + value.ordinal).toRelative()
                                                                    text = value.name.toAbsolute()
                                                                    fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                                                    scale = 0.015.toRelative()
                                                                }
                                                    }
                                                })

                                        stateId = "state"

                                        storedState += "state"

                                        onInit = {
                                            it.second["state"] = "view"
                                        }

                                        onStateUpdate["selected"] = { state ->
                                            if (state["selected"] != null) {
                                                state["state"] = if (state["selected"] as Boolean) { "edit" } else { "view" }
                                            }
                                        }
                                    }
                        }
            }*/
            is DisplayedSetting.ColorPicker -> {
                return Container()
                    .apply {
                        children += TabHolder()
                            .apply {
                                addChild("edit", Container()
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
                                            }

                                        children += Container()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = 0.066.toRelative()
                                            }

                                        children += List(com.github.sorusclient.client.ui.framework.List.HORIZONTAL)
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = Relative(1.7, true)

                                                backgroundColor = Color.fromRGB(0, 0, 0, 65).toAbsolute()
                                                borderThickness = 0.4.toAbsolute()
                                                borderColor = Color.fromRGB(0, 0, 0, 100).toAbsolute()
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

                                                            setting.setting.overriden = true
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

                                                            setting.setting.overriden = true
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

                                                            setting.setting.overriden = true
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
                                            }
                                    })

                                addChild("view", Container()
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
                                            }

                                        children += Container()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = 0.066.toRelative()
                                            }

                                        children += Container()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                width = Copy()
                                                height = 0.6.toRelative()

                                                backgroundColor = { _: Map<String, Any> ->
                                                    setting.setting.value
                                                }.toDependent()

                                                backgroundCornerRadius = 0.01.toRelative()
                                            }
                                    })

                                onStateUpdate["value"] = { state ->
                                    val stateValue = state["value"] as FloatArray
                                    val rgb = java.awt.Color.HSBtoRGB(stateValue[0], stateValue[1], stateValue[2])
                                    val javaColor = java.awt.Color(rgb)
                                    setting.setting.realValue = Color.fromRGB(
                                        javaColor.red,
                                        javaColor.green,
                                        javaColor.blue,
                                        (stateValue[3] * 255).toInt()
                                    )
                                }

                                onStateUpdate["selected"] = { state ->
                                    if (state["selected"] != null) {
                                        state["colorTab"] = if (state["selected"] as Boolean) "edit" else "view"
                                    }
                                }

                                storedState += "value"
                                val color = setting.setting.value
                                val javaColor = java.awt.Color(
                                    (color.red * 255).toInt(),
                                    (color.green * 255).toInt(),
                                    (color.blue * 255).toInt(),
                                    (color.alpha * 255).toInt()
                                )

                                val colorData = FloatArray(4)
                                java.awt.Color.RGBtoHSB(javaColor.red, javaColor.green, javaColor.blue, colorData)
                                colorData[3] = color.alpha.toFloat()
                                runtime.setState("value", colorData)

                                stateId = "colorTab"

                                x = Side.NEGATIVE.toSide()
                                width = 0.75.toRelative()
                            }

                        storedState += "colorTab"

                        onInit += { state ->
                            state.second["colorTab"] = "view"
                        }

                        height = { state: Map<String, Any> ->
                            Absolute(if (state["colorTab"] == "edit") 30.0 else 15.0)
                        }.toDependent()
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

    private fun initializeUserInterface() {
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

                                backgroundColor = Color.fromRGB(15, 15, 15, 200).toAbsolute()
                                borderColor = Color.fromRGB(10, 10, 10, 150).toAbsolute()
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
                                            })

                                        addChild(Container()
                                            .apply {
                                                width = 0.8.toRelative()
                                                height = 0.6.toAbsolute()
                                                setPadding(0.1.toRelative())

                                                backgroundColor = Color.fromRGB(10, 10, 10, 150).toAbsolute()
                                            })

                                        val tabs = arrayOf("home", "settings")

                                        for (tab in tabs) {
                                            addChild(Container()
                                                .apply {
                                                    width = 0.65.toRelative()
                                                    height = 1.0.toCopy()
                                                    setPadding(0.175.toRelative())

                                                    backgroundCornerRadius = 0.15.toRelative()

                                                    backgroundColor = Dependent { state ->
                                                        return@Dependent if (state["tab"] == tab || (tab == "home" && state["tab"] == null)) {
                                                            Color.fromRGB(60, 75, 250, 65)
                                                        } else {
                                                            Color.fromRGB(0, 0, 0, 65)
                                                        }
                                                    }

                                                    borderColor = Dependent { state ->
                                                        return@Dependent if (state["tab"] == tab || (tab == "home" && state["tab"] == null) || state["hovered"] as Boolean) {
                                                            Color.fromRGB(60, 75, 250, 255)
                                                        } else {
                                                            Color.fromRGB(10, 10, 10, 150)
                                                        }
                                                    }

                                                    borderThickness = 0.4.toAbsolute()

                                                    children += Container()
                                                        .apply {
                                                            width = 0.5.toRelative()
                                                            height = 0.5.toRelative()

                                                            backgroundImage = "sorus/ui/navbar/$tab.png".toAbsolute()
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

                                                backgroundColor = Color.fromRGB(15, 15, 15, 200).toAbsolute()
                                                borderColor = Color.fromRGB(10, 10, 10, 150).toAbsolute()
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
                                                                            Color.fromRGB(60, 75, 250, 65)
                                                                        } else {
                                                                            Color.fromRGB(0, 0, 0, 65)
                                                                        }
                                                                    }
                                                                    borderThickness = 0.4.toAbsolute()
                                                                    borderColor = Dependent { state ->
                                                                        if ((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) {
                                                                            Color.fromRGB(60, 75, 250, 255)
                                                                        } else {
                                                                            Color.fromRGB(0, 0, 0, 100)
                                                                        }
                                                                    }

                                                                    children += Container()
                                                                        .apply {
                                                                            x = Side.NEGATIVE.toSide()
                                                                            width = 1.0.toCopy()
                                                                            height = 0.5.toRelative()
                                                                            setPadding(Relative(0.2, true))

                                                                            backgroundImage = "sorus/ui/profiles/create.png".toAbsolute()
                                                                        }

                                                                    children += Text()
                                                                        .apply {
                                                                            x = Side.NEGATIVE.toSide()
                                                                            setPadding(Relative(0.2, true))

                                                                            scale = 0.012.toRelative()
                                                                            fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                            text = "Create".toAbsolute()
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
                                                                            Color.fromRGB(60, 75, 250, 65)
                                                                        } else {
                                                                            Color.fromRGB(0, 0, 0, 65)
                                                                        }
                                                                    }
                                                                    borderThickness = 0.4.toAbsolute()
                                                                    borderColor = Dependent { state ->
                                                                        if ((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) {
                                                                            Color.fromRGB(60, 75, 250, 255)
                                                                        } else {
                                                                            Color.fromRGB(0, 0, 0, 100)
                                                                        }
                                                                    }

                                                                    children += Container()
                                                                        .apply {
                                                                            x = Side.NEGATIVE.toSide()
                                                                            width = 1.0.toCopy()
                                                                            height = 0.5.toRelative()
                                                                            setPadding(Relative(0.2, true))

                                                                            backgroundImage = "sorus/ui/profiles/delete.png".toAbsolute()
                                                                        }

                                                                    children += Text()
                                                                        .apply {
                                                                            x = Side.NEGATIVE.toSide()
                                                                            y = Side.ZERO.toSide()
                                                                            setPadding(Relative(0.2, true))

                                                                            scale = 0.012.toRelative()
                                                                            fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                            text = "Delete".toAbsolute()
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

                                                            backgroundColor = Color.fromRGB(10, 10, 10, 150).toAbsolute()
                                                        }

                                                    children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                                        .apply {
                                                            setPadding(0.075.toRelative())

                                                            onInit += {
                                                                clear()

                                                                val profiles: MutableList<Pair<Profile, Int>> = ArrayList()
                                                                addProfiles(SettingManager.mainProfile, 0, profiles)

                                                                for (profile in profiles) {
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
                                                                                            Color.fromRGB(60, 75, 250, 65)
                                                                                        } else {
                                                                                            Color.fromRGB(0, 0, 0, 65)
                                                                                        }
                                                                                    }

                                                                                    borderColor = Dependent { state ->
                                                                                        return@Dependent if (SettingManager.currentProfile == profile.first || state["hovered"] as Boolean) {
                                                                                            Color.fromRGB(60, 75, 250, 255)
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
                                                                onInit(com.github.sorusclient.client.util.Pair(this, HashMap()))
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
                                                    onInit(com.github.sorusclient.client.util.Pair(this, HashMap()))
                                                }

                                                storedState += "hasInitProfiles"
                                            }

                                        children += Container()
                                            .apply {
                                                backgroundCornerRadius = 0.0155.toRelative()
                                                setPadding(0.0125.toRelative())

                                                backgroundColor = Color.fromRGB(15, 15, 15, 200).toAbsolute()
                                                borderColor = Color.fromRGB(10, 10, 10, 150).toAbsolute()
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
                                                                }

                                                            /*children += Container()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()
                                                                    width = 0.5.toRelative()
                                                                    height = 0.7.toRelative()



                                                                    backgroundColor = Color.WHITE.toAbsolute()
                                                                }*/

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
                                                                                Color.fromRGB(60, 75, 250, 65)
                                                                            } else {
                                                                                Color.fromRGB(0, 0, 0, 65)
                                                                            }
                                                                        }
                                                                        borderThickness = 0.4.toAbsolute()
                                                                        borderColor = Dependent { state ->
                                                                            if ((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) {
                                                                                Color.fromRGB(60, 75, 250, 255)
                                                                            } else {
                                                                                Color.fromRGB(0, 0, 0, 100)
                                                                            }
                                                                        }

                                                                        children += Container()
                                                                            .apply {
                                                                                width = 0.5.toRelative()
                                                                                height = 0.5.toRelative()

                                                                                backgroundImage = "sorus/ui/settings/back.png".toAbsolute()
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
                                                            addChild(List(com.github.sorusclient.client.ui.framework.List.GRID)
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
                                                                                            Color.fromRGB(60, 75, 250, 65)
                                                                                        } else {
                                                                                            Color.fromRGB(0, 0, 0, 65)
                                                                                        }
                                                                                    }
                                                                                    borderThickness = 0.4.toAbsolute()
                                                                                    borderColor = Color.fromRGB(10, 10, 10, 150).toAbsolute()
                                                                                    borderColor = Dependent { state ->
                                                                                        if ((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) {
                                                                                            Color.fromRGB(60, 75, 250, 255)
                                                                                        } else {
                                                                                            Color.fromRGB(0, 0, 0, 100)
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

                                                                    backgroundColor = Color.fromRGB(10, 10, 10, 150).toAbsolute()
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
                                                                                            Color.fromRGB(60, 75, 250, 65)
                                                                                        } else {
                                                                                            Color.fromRGB(0, 0, 0, 65)
                                                                                        }
                                                                                    }
                                                                                    borderThickness = 0.4.toAbsolute()
                                                                                    borderColor = Dependent { state ->
                                                                                        if ((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) {
                                                                                            Color.fromRGB(60, 75, 250, 255)
                                                                                        } else {
                                                                                            Color.fromRGB(0, 0, 0, 100)
                                                                                        }
                                                                                    }

                                                                                    children += Text()
                                                                                        .apply {
                                                                                            scale = 0.011.toRelative()
                                                                                            fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                                                                            text = "Reset".toAbsolute()
                                                                                        }

                                                                                    onUpdate += { state ->
                                                                                        var setting = setting
                                                                                        while (setting is DisplayedSetting.Dependent<*>) {
                                                                                            setting = setting.configurableData
                                                                                        }

                                                                                        if (setting is DisplayedSetting.ConfigurableDataSingleSetting<*>) {
                                                                                            state["hidden"] = !setting.setting.overriden || SettingManager.currentProfile == SettingManager.mainProfile
                                                                                        }
                                                                                    }

                                                                                    onClick = {
                                                                                        var setting = setting
                                                                                        while (setting is DisplayedSetting.Dependent<*>) {
                                                                                            setting = (setting as DisplayedSetting.Dependent<*>).configurableData
                                                                                        }

                                                                                        if (setting is DisplayedSetting.ConfigurableDataSingleSetting<*>) {
                                                                                            (setting as DisplayedSetting.ConfigurableDataSingleSetting<*>).setting.overriden = false
                                                                                        }
                                                                                    }
                                                                                }
                                                                        })
                                                                }
                                                            }
                                                        }
                                                }

                                                for (onInit in onInit) {
                                                    onInit(com.github.sorusclient.client.util.Pair(this, HashMap()))
                                                }
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
                        var searchResults: List<SearchResult> = ArrayList()

                        width = 0.25.toRelative()
                        height = {
                            (0.12 + if (searchResults.isNotEmpty()) { 0.032 } else { 0.0 } + (searchResults.size * 0.138)).toCopy()
                        }.toDependent()

                        backgroundCornerRadius = 0.01.toRelative()

                        borderThickness = 0.4.toAbsolute()
                        backgroundColor = Color.fromRGB(15, 15, 15, 200).toAbsolute()
                        borderColor = Color.fromRGB(10, 10, 10, 150).toAbsolute()

                        storedState += "searchParameter"
                        storedState += "searchResults"
                        storedState += "selectedResult"

                        onInit += { state ->
                            state.second["searchParameter"] = ""
                        }

                        val results = ArrayList<SearchResult>()
                        addSettingResults(results, SettingManager.mainUICategory, "")

                        results.add(MenuSearchResult("home", "Home"))
                        results.add(MenuSearchResult("settings", "Settings"))

                        for (serverJsonString in AssetUtil.getAllServerJson()) {
                            val serverJson = JSONObject(serverJsonString)
                            results.add(ServerSearchResult(serverJson["name"] as String, serverJson["ip"] as String))
                        }

                        onStateUpdate["searchParameter"] = { state ->
                            searchResults = search(state["searchParameter"] as String, results, 2.0, 5)
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

                                        onKey = { state ->
                                            var parameter = if (state.first["searchParameter"] == null) { "" } else { state.first["searchParameter"] as String }

                                            var string = "abcdefghijklmnopqrstuvwxyz"
                                            string += string.uppercase()

                                            if (string.contains(state.second.second)) {
                                                parameter += state.second.second
                                            } else if (state.second.first == Key.BACKSPACE && parameter.isNotEmpty()) {
                                                parameter = parameter.substring(0, parameter.length - 1)
                                            } else if (state.second.first == Key.SPACE) {
                                                parameter += " "
                                            } else if (state.second.first == Key.ARROW_DOWN) {
                                                var newSelectedResult = state.first.getOrDefault("selectedResult", 0) as Int
                                                newSelectedResult++
                                                if (newSelectedResult > searchResults.size - 1) {
                                                    newSelectedResult = 0
                                                }
                                                state.first["selectedResult"] = newSelectedResult
                                            } else if (state.second.first == Key.ARROW_UP) {
                                                var newSelectedResult = state.first.getOrDefault("selectedResult", 0) as Int
                                                newSelectedResult--
                                                if (newSelectedResult < 0) {
                                                    newSelectedResult = searchResults.size - 1
                                                }
                                                state.first["selectedResult"] = newSelectedResult
                                            } else if (state.second.first == Key.ENTER) {
                                                searchResults[state.first.getOrDefault("selectedResult", 0) as Int].onSelect()
                                            }

                                            prevKeyTime = System.currentTimeMillis()

                                            state.first["searchParameter"] = parameter
                                        }
                                    }

                                children += Container()
                                    .apply {
                                        width = 0.9.toRelative()
                                        height = 0.6.toAbsolute()

                                        backgroundColor = Color.fromRGB(10, 10, 10, 150).toAbsolute()
                                    }

                                onStateUpdate["searchParameter"] = {
                                    clearAfter(2)

                                    for ((i, result) in searchResults.withIndex()) {
                                        children += Container()
                                            .apply {
                                                height = 0.12.toCopy()

                                                setPadding(0.025.toRelative())

                                                backgroundCornerRadius = 0.025.toRelative()

                                                backgroundColor = Color.fromRGB(0, 0, 0, 65).toAbsolute()
                                                borderThickness = 0.4.toAbsolute()
                                                borderColor = { state: Map<String, Any> ->
                                                    if (state.getOrDefault("selectedResult", 0) == i) {
                                                        Color.fromRGB(60, 75, 250, 255)
                                                    } else {
                                                        Color.fromRGB(0, 0, 0, 100)
                                                    }
                                                }.toDependent()

                                                children += Text()
                                                    .apply {
                                                        x = Side.NEGATIVE.toSide()
                                                        paddingLeft = 0.05.toRelative()

                                                        fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                                        scale = 0.006.toRelative()
                                                        text = result.displayName.toAbsolute()
                                                    }
                                            }
                                    }
                                }
                            }
                    }
            }
    }

    private fun addSettingResults(list: MutableList<SearchResult>, category: DisplayedCategory, name: String) {
        list.add(SettingSearchResult(name, category))

        for (displayed in category.components) {
            val name = name + "/" + displayed.name
            if (displayed is DisplayedCategory) {
                addSettingResults(list, displayed, name)
            } else {
                list.add(SettingSearchResult(name, displayed))
            }
        }
    }

    private fun search(searchTerm: String, results: List<SearchResult>, minimum: Double, maxResults: Int): List<SearchResult> {
        val scores: MutableList<Pair<SearchResult, Double>> = ArrayList()

        val searchTerm = searchTerm.lowercase()

        for (result in results) {
            var score = 0
            for (i in 1..searchTerm.length) {
                for (j in 0..searchTerm.length - i) {
                    if (result.searchString.lowercase().contains(searchTerm.substring(j, j + i))) {
                        score += i.toDouble().pow(2).toInt()
                    }
                }
            }

            scores.add(Pair(result, score.toDouble() / (sqrt(result.searchString.length.toDouble()) * searchTerm.length.toDouble())))
        }

        scores.retainAll {
            return@retainAll it.second > minimum
        }

        scores.sortBy {
            it.second
        }

        scores.reverse()

        var added = 0
        scores.retainAll {
            return@retainAll if (added < maxResults) {
                added++
                true
            } else {
                false
            }

        }

        val addedResults = ArrayList<String>()
        scores.retainAll {
            return@retainAll if (addedResults.contains(it.first.displayName)) {
                false
            } else {
                addedResults.add(it.first.displayName)
                true
            }
        }

        return scores.map {
            it.first
        }
    }

    abstract class SearchResult(val searchString: String, val displayName: String) {
        abstract fun onSelect()
    }

    class SettingSearchResult(searchString: String, displayed: Displayed) : SearchResult(searchString, if (displayed is DisplayedCategory) { displayed.name } else { displayed.parent!!.name }) {

        private val linkedCategory: DisplayedCategory

        init {
             linkedCategory = if (displayed is DisplayedCategory) {
                displayed
            } else {
                displayed.parent!!
             }
        }

        override fun onSelect() {
            mainGui.apply {
                children[0].apply {
                    runtime.setState("tab", "settings")
                    runtime.setState("currentSettingsCategory", linkedCategory)
                    runtime.setState("resetSettingsScreen", false)
                }
            }

            ContainerRenderer.open(mainGui)
        }

    }

    class MenuSearchResult(val menu: String, name: String) : SearchResult(name, name) {

        override fun onSelect() {
            mainGui.apply {
                children[0].apply {
                    runtime.setState("tab", menu)
                }
            }

            ContainerRenderer.open(mainGui)
        }

    }

    class ServerSearchResult(name: String, val ip: String) : SearchResult(name, name) {

        override fun onSelect() {
            ContainerRenderer.close()
            AdapterManager.getAdapter().openScreen(ScreenType.IN_GAME)
            AdapterManager.getAdapter().joinServer(ip)
        }

    }

    private fun addProfiles(profile: Profile, index: Int, profiles: MutableList<Pair<Profile, Int>>) {
        profiles.add(Pair(profile, index))
        for (child in profile.children) {
            addProfiles(child.value, index + 1, profiles)
        }
    }

}
