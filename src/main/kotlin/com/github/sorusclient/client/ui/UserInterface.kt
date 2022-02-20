package com.github.sorusclient.client.ui

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.ScreenType
import com.github.sorusclient.client.adapter.event.InitializeEvent
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.adapter.event.RenderEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.*
import com.github.sorusclient.client.setting.DisplayedSetting.*
import com.github.sorusclient.client.ui.framework.*
import com.github.sorusclient.client.ui.framework.constraint.*
import com.github.sorusclient.client.ui.framework.constraint.Dependent
import com.github.sorusclient.client.util.Color
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.set
import kotlin.math.ceil

object UserInterface {

    private val hudEditScreenOpen = AtomicBoolean(false)
    private lateinit var mainGui: Container
    private val guiOpened = AtomicBoolean(false)

    fun initialize() {
        val eventManager = EventManager
        eventManager.register { event: KeyEvent ->
            val adapter = AdapterManager.getAdapter()
            if (!event.isRepeat) {
                if (event.key === Key.SHIFT_RIGHT && event.isPressed && adapter.openScreen === ScreenType.IN_GAME) {
                    adapter.openScreen(ScreenType.DUMMY)
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
    }

    private fun getSetting(setting: DisplayedSetting?): Container {
        when (setting) {
            is Toggle -> {
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

                        children += Container()
                            .apply {
                                x = Side.NEGATIVE.toSide()
                                width = 2.0.toCopy()
                                height = 0.6.toRelative()
                                setPadding(Relative(0.2, true))

                                backgroundColor = Dependent { state ->
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
                                    if (state["hovered"] as Boolean || toggled) {
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
            is Slider -> {
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
                                            Relative((setting.setting.value.toDouble() - minimum) / (maximum - minimum))
                                        }.toDependent()
                                        height = 1.1.toRelative()

                                        //backgroundColor = Color.fromRGB(60, 75, 250, 255).toAbsolute()
                                        //backgroundCornerRadius = Relative(0.5, true)
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

                                        backgroundColor = Color.WHITE.toAbsolute()

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
            is KeyBind -> {
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
                                        fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
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
            is ClickThrough -> {
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
                                        fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
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
            is ColorPicker -> {
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
                                                fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
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
                                                fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
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

                                                            onInit[1](com.github.sorusclient.client.util.Pair(this, HashMap()))

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
                                                storedState += "currentSettingsCategory"

                                                onStateUpdate["hasInitSettings"] = { state ->
                                                    if (state["hasInitSettings"] == false) {
                                                        state["hasInitSettings"] = true
                                                        state["hasInit"] = false
                                                    }
                                                }

                                                onInit += { state ->
                                                    if (state.second["currentSettingsCategory"] == null) {
                                                        state.second["currentSettingsCategory"] = SettingManager.mainCategory
                                                    }

                                                    val category = state.second["currentSettingsCategory"] as DisplayedCategory

                                                    children.clear()

                                                    children += Container()
                                                        .apply {
                                                            y = Side.NEGATIVE.toSide()
                                                            height = 0.05.toRelative()
                                                            setPadding(0.025.toRelative())

                                                            children += Container()
                                                                .apply {
                                                                    x = Side.NEGATIVE.toSide()
                                                                    width = 0.5.toRelative()
                                                                    height = 0.7.toRelative()

                                                                    children += Text()
                                                                        .apply {
                                                                            x = Side.NEGATIVE.toSide()

                                                                            fontRenderer = "sorus/ui/font/Quicksand-Bold.ttf".toAbsolute()
                                                                            scale = 0.0062.toRelative()
                                                                            text = "Settings | ${category.id}".toAbsolute()
                                                                        }
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
                                                                    for (displayed in category.displayed) {
                                                                        if (displayed is DisplayedCategory) {
                                                                            count++
                                                                        }
                                                                    }

                                                                    height = Relative(ceil(count / 3.0) * 0.06 + (ceil(count / 3.0) + 1) * 0.015, true)

                                                                    for (displayed in category.displayed) {
                                                                        if (displayed is DisplayedCategory) {
                                                                            addChild(Container()
                                                                                .apply {
                                                                                    width = 0.30666.toRelative()
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

                                                                                            //backgroundImage = "sorus/ui/grass_block.png".toAbsolute()
                                                                                        }

                                                                                    children += Text()
                                                                                        .apply {
                                                                                            x = Side.NEGATIVE.toSide()

                                                                                            scale = 0.006.toRelative()
                                                                                            fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                                                                            text = displayed.id.toAbsolute()
                                                                                        }

                                                                                    onClick = { state ->
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

                                                            for (displayed in category.displayed) {
                                                                if (displayed is DisplayedSetting) {
                                                                    addChild(getSetting(displayed)
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
                                                                                        if (displayed is ConfigurableDataSingleSetting<*>) {
                                                                                            state["hidden"] = !displayed.setting.overriden || SettingManager.currentProfile == SettingManager.mainProfile
                                                                                        }
                                                                                    }

                                                                                    onClick = {
                                                                                        if (displayed is ConfigurableDataSingleSetting<*>) {
                                                                                            displayed.setting.overriden = false
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
                            }

                        storedState += "tab"
                    }
            }
    }

    private fun addProfiles(profile: Profile, index: Int, profiles: MutableList<Pair<Profile, Int>>) {
        profiles.add(Pair(profile, index))
        for (child in profile.children) {
            addProfiles(child.value, index + 1, profiles)
        }
    }

    /*private fun initializeUserInterface() {
        mainGui = TabHolder()
            .apply {
                stateId = "currentTab"

                val tabs = arrayOf("home", "hudEdit", "moduleEdit", "settingEdit", "profileEdit")
                for (tab in tabs) {
                    val container1 = Container()
                    addNavBar(container1, tabs)
                    if (tab == "moduleEdit") {
                        addModulesScreen(container1)
                    } else if (tab == "settingEdit") {
                        addSettingsScreen(container1)
                    }
                    addChild(tab, container1)
                }

                onInit = { state ->
                    state.first.storedState += "currentTab"
                    state.second["currentTab"] = "home"
                }

                onStateUpdate["currentTab"] = { state ->
                    hudEditScreenOpen.set(
                        state["currentTab"] == "hudEdit"
                    )
                }
            }
    }

    private fun addNavBar(container: Container, tabs: Array<String>) {
        container.addChild(
            Container().apply {
                    y = Side.POSITIVE.toSide()
                    width = 0.53.toRelative()
                    height = 0.09.toRelative()
                    setPadding(0.01.toRelative()

                    backgroundCornerRadius = 0.0075.toRelative()
                    backgroundColor = Color.fromRGB(26, 26, 26, 230).toAbsolute()

                    children += Container().apply {
                        x = Side.NEGATIVE.toSide()
                        width = Copy()
                        height = 0.7.toRelative()
                        setPadding(0.02.toRelative()
                        backgroundImage = "assets/minecraft/sorus.png".toAbsolute()
                    }

                    children += List(com.github.sorusclient.client.ui.framework.List.HORIZONTAL).apply {
                        x = Side.ZERO.toSide()
                        y = Side.ZERO.toSide()
                        height = 0.7.toRelative()

                        for (tab in tabs) {
                            children += Container().apply {
                                width = Copy()
                                setPadding(0.01.toRelative()
                                backgroundCornerRadius = 0.0075.toRelative()
                                backgroundColor = { state: Map<String, Any> ->
                                    if (state["currentTab"] == tab) {
                                        Color.fromRGB(20, 118, 188, 255)
                                    } else {
                                        Color.fromRGB(24, 24, 24, 255)
                                    }
                                }.toDependent()

                                onClick = { state ->
                                    state["currentTab"] = tab
                                }

                                children += Container().apply {
                                    width = 0.5.toRelative()
                                    height = 0.5.toRelative()
                                    backgroundImage = "assets/minecraft/$tab.png".toAbsolute()
                                }
                            }

                            children += Container().apply {
                                width = 0.1.toCopy()
                            }
                        }
                    }
                })
    }

    private fun addSettingsScreen(container: Container) {
        container.apply {
            children += Container().apply {
                onInit = { state ->
                    state.first.storedState += "editingCategory"
                    state.first.storedState += "hasInitMain"

                    onStateUpdate["hasInitMain"] = { state ->
                        if (!(state["hasInitMain"] as Boolean)) {
                            state["hasInit"] = false
                            state["hasInitMain"] = true
                        }
                    }

                    clear()

                    children += Container()
                        .apply {
                            y = Side.NEGATIVE.toSide()
                            width = 0.53.toRelative()
                            setPadding(0.01.toRelative()
                            backgroundCornerRadius = 0.0075.toRelative()
                            backgroundColor = Color.fromRGB(26, 26, 26, 230).toAbsolute()

                            val category = if (state.second["editingCategory"] != null) {
                                state.second["editingCategory"] as DisplayedCategory
                            } else {
                                SettingManager.mainCategory
                            }

                            children += Container()
                                .apply {
                                    y = Side.NEGATIVE.toSide()
                                    height = 0.05.toCopy()
                                    setPadding(0.005.toRelative()

                                    children += Text()
                                        .apply {
                                            x = Side.NEGATIVE.toSide()
                                            fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                            setPadding(0.01.toRelative()
                                            text = "Settings".toAbsolute()
                                            scale = 0.003.toRelative()
                                        }

                                    if (category.parent != null) {
                                        children += Container()
                                            .apply {
                                                x = Side.POSITIVE.toSide()
                                                width = 1.0.toCopy()

                                                setPadding(0.01.toRelative()

                                                backgroundColor = Color.fromRGB(255, 255, 255, 200).toAbsolute()

                                                onClick = { state ->
                                                    state["hasInitMain"] = false
                                                    state["editingCategory"] = category.parent!!
                                                }
                                            }
                                    }
                                }

                            children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                .apply {
                                    scissor = true

                                    children += List(com.github.sorusclient.client.ui.framework.List.GRID)
                                        .apply {
                                            columns = 3
                                            setPadding(0.005.toRelative()

                                            var count = 0;
                                            for (setting in category.displayed) {
                                                if (setting is DisplayedCategory) {
                                                    count += 1
                                                }
                                            }

                                            height = (ceil(count / 3.0) * 0.11).toCopy()

                                            for (setting in category.displayed) {
                                                if (setting is DisplayedCategory) {
                                                    addChild(Container()
                                                        .apply {
                                                            backgroundColor = Color.fromRGB(0, 0 , 0, 100).toAbsolute()

                                                            width = 0.25.toRelative()
                                                            height = 0.4.toCopy()
                                                            setPadding(0.005.toRelative()

                                                            onClick = { state ->
                                                                state["hasInitMain"] = false
                                                                state["editingCategory"] = setting
                                                            }

                                                            children += Text()
                                                                .apply {
                                                                    fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                                                    text = setting.id.toAbsolute()
                                                                    scale = 0.007.toRelative()
                                                                }
                                                        })
                                                }
                                            }
                                        }

                                    children += List(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                        .apply {
                                            setPadding(0.005.toRelative()

                                            for (setting in category.displayed) {
                                                if (setting is DisplayedSetting) {
                                                    val settingContainer: Container = getSetting(setting) as Container

                                                    if (setting is ConfigurableDataSingleSetting<*>) {
                                                        settingContainer.apply {
                                                            children += Container()
                                                                .apply {
                                                                    backgroundColor = Color.fromRGB(26, 26, 26, 230).toAbsolute()

                                                                    onUpdate += { state ->
                                                                        state["hidden"] =
                                                                            !setting.setting.overriden || SettingManager.currentProfile == SettingManager.mainProfile
                                                                    }

                                                                    children += Text()
                                                                        .apply {
                                                                            fontRenderer =
                                                                                "Quicksand-Medium.ttf".toAbsolute()
                                                                            text = "reset to default".toAbsolute()
                                                                            scale = 0.0025.toRelative()
                                                                        }

                                                                    onClick = { state ->
                                                                        setting.setting.overriden = false
                                                                    }
                                                                }
                                                        }
                                                    }

                                                    addChild(settingContainer)
                                                }
                                            }
                                        }
                                }
                        }

                    children += Container()
                        .apply {
                            x = Side.NEGATIVE.toSide()
                            setPadding(0.01.toRelative()
                            backgroundCornerRadius = 0.0075.toRelative()
                            backgroundColor = Color.fromRGB(26, 26, 26, 230).toAbsolute()

                            children += Container()
                                .apply {
                                    y = Side.NEGATIVE.toSide()
                                    height = 0.15.toCopy()
                                    setPadding(0.012.toRelative()

                                    children += Text()
                                        .apply {
                                            x = Side.NEGATIVE.toSide()
                                            fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                            setPadding(0.03.toRelative()
                                            text = "Profiles".toAbsolute()
                                            scale = 0.008.toRelative()
                                        }
                                }

                            children += List(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                .apply {
                                    y = Side.NEGATIVE.toSide()

                                    for (profile in SettingManager.getAllProfiles()) {
                                        addChild(Container()
                                            .apply {
                                                height = 0.125.toCopy()

                                                backgroundColor = Dependent { state ->
                                                    return@Dependent if (SettingManager.currentProfile == profile) {
                                                        Color.fromRGB(26, 26, 150, 230)
                                                    } else {
                                                        Color.fromRGB(26, 26, 26, 230)
                                                    }
                                                }

                                                children += Text()
                                                    .apply {
                                                        fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                                        scale = 0.008.toRelative()
                                                        text = profile.id.toAbsolute()
                                                    }

                                                onClick = { state ->
                                                    SettingManager.load(profile)
                                                    state["hasInitMain"] = false
                                                }
                                            })
                                    }
                                }
                        }
                }

            }

            children += Container()
                .apply {
                    x = Side.NEGATIVE.toSide()
                    setPadding(0.01.toRelative()
                    backgroundCornerRadius = 0.0075.toRelative()
                    backgroundColor = Color.fromRGB(26, 26, 26, 230).toAbsolute()
                }
        }
    }

    private fun addModulesScreen(container: Container) {
        container.apply {
            children += TabHolder().apply {
                addChild("main", Container()
                    .apply {
                        y = Side.NEGATIVE.toSide()
                        width = 0.53.toRelative()
                        setPadding(0.01.toRelative()
                        backgroundCornerRadius = 0.0075.toRelative()
                        backgroundColor = Color.fromRGB(26, 26, 26, 230).toAbsolute()

                        children += Container()
                            .apply {
                                y = Side.NEGATIVE.toSide()
                                height = 0.05.toCopy()
                                setPadding(0.005.toRelative()

                                children += Text()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                        setPadding(0.01.toRelative()
                                        text = "Module".toAbsolute()
                                        scale = 0.003.toRelative()
                                    }
                            }

                        children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                            .apply {
                                for (module in ModuleManager.modules.values) {
                                    children += Container()
                                        .apply {
                                            height = 0.1.toCopy()
                                            setPadding(0.01.toRelative()
                                            backgroundCornerRadius = 0.01.toRelative()
                                            backgroundColor = Color.fromRGB(24, 24, 24, 255).toAbsolute()

                                            children += Container()
                                                .apply {
                                                    x = Side.NEGATIVE.toSide()
                                                    width = Copy()
                                                    height = 0.6.toRelative()
                                                    setPadding(Relative(0.2, true)
                                                    backgroundCornerRadius = 0.01.toRelative()
                                                    backgroundColor = Color.fromRGB(255, 255, 255, 200).toAbsolute()
                                                }

                                            children += Text()
                                                .apply {
                                                    x = Side.NEGATIVE.toSide()
                                                    y = Side.NEGATIVE.toSide()
                                                    setPadding(Relative(0.15, true)

                                                    text = module.name.toAbsolute()
                                                    fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                                    scale = 0.003.toRelative()
                                                }

                                            children += Text()
                                                .apply {
                                                    x = Side.NEGATIVE.toSide()
                                                    y = Side.POSITIVE.toSide()
                                                    setPadding(Relative(0.15, true)

                                                    text = module.description.toAbsolute()
                                                    fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                                    scale = 0.0025.toRelative()
                                                    textColor = Color.fromRGB(255, 255, 255, 80).toAbsolute()
                                                }

                                            children += Container()
                                                .apply {
                                                    x = Side.POSITIVE.toSide()
                                                    y = Side.ZERO.toSide()
                                                    width = Copy()
                                                    height = 0.4.toRelative()
                                                    setPadding(Relative(0.3, true)

                                                    backgroundImage = "assets/minecraft/gear.png".toAbsolute()
                                                    backgroundColor = Color.fromRGB(255, 255, 255, 75).toAbsolute()

                                                    onClick =  { state ->
                                                        state["moduleScreen"] = "edit"
                                                        state["currentEditingModule"] = module
                                                    }
                                                }
                                        }
                                }

                                scissor = true
                            }
                    })

                addChild("edit", Container()
                    .apply {
                        y = Side.NEGATIVE.toSide()
                        width = 0.53.toRelative()
                        setPadding(0.01.toRelative()
                        backgroundCornerRadius = 0.0075.toRelative()
                        backgroundColor = Color.fromRGB(26, 26, 26, 230).toAbsolute()

                        onInit = { state ->
                            val moduleData = state.second["currentEditingModule"] as ModuleData
                            val settings: MutableList<DisplayedSetting> = ArrayList()

                            moduleData.module.addSettings(settings)

                            state.first.apply {
                                clear()

                                children += Container()
                                    .apply {
                                        y = Side.NEGATIVE.toSide()
                                        height = 0.05.toCopy()
                                        setPadding(0.005.toRelative()

                                        children += Text()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                setPadding(0.01.toRelative()

                                                fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                                text = moduleData.name.toAbsolute()
                                                scale = 0.003.toRelative()
                                            }
                                    }

                                children += Container()
                                    .apply {
                                        x = Side.POSITIVE.toSide()
                                        y = Side.POSITIVE.toSide()
                                        width = 25.0.toAbsolute()
                                        height = 25.0.toAbsolute()

                                        backgroundColor = Color.WHITE.toAbsolute()
                                        onClick = { state ->
                                            state["currentModuleTab"] = "main"
                                        }
                                    }

                                children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                                    .apply {
                                        y = Side.NEGATIVE.toSide()
                                        scissor = true

                                        addSettingsList(this, settings)
                                    }
                            }
                        }
                    })

                stateId = "moduleScreen"

                onInit = { state ->
                    state.first.storedState += "moduleScreen"
                    state.first.storedState += "currentEditingModule"
                    state.second["moduleScreen"] = "main"
                }
            }

            children += Container()
                .apply {
                    x = Side.NEGATIVE.toSide()
                    setPadding(0.01.toRelative()
                    backgroundCornerRadius = 0.0075.toRelative()
                    backgroundColor = Color.fromRGB(26, 26, 26, 230).toAbsolute()
                }
        }
    }*/

    fun isHudEditScreenOpen(): Boolean {
        return hudEditScreenOpen.get() && guiOpened.get()
    }
}
