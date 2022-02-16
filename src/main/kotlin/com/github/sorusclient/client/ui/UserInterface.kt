package com.github.sorusclient.client.ui

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.ScreenType
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.adapter.event.RenderEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.module.ModuleData
import com.github.sorusclient.client.module.ModuleManager
import com.github.sorusclient.client.setting.ConfigurableData
import com.github.sorusclient.client.setting.ConfigurableData.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.ui.framework.*
import com.github.sorusclient.client.ui.framework.constraint.*
import com.github.sorusclient.client.util.Color
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.List
import kotlin.collections.set

object UserInterface {

    private val hudEditScreenOpen = AtomicBoolean(false)
    private var mainGui: Container? = null
    private val guiOpened = AtomicBoolean(false)

    fun initialize() {
        initializeUserInterface()
        val eventManager = EventManager
        eventManager.register { event: KeyEvent ->
            val adapter = AdapterManager.getAdapter()
            if (!event.isRepeat) {
                if (event.key === Key.P && event.isPressed && adapter.openScreen === ScreenType.IN_GAME) {
                    adapter.openScreen(ScreenType.DUMMY)
                    guiOpened.set(true)
                } else if (event.key === Key.ESCAPE && event.isPressed && adapter.openScreen === ScreenType.DUMMY) {
                    adapter.openScreen(ScreenType.IN_GAME)
                    guiOpened.set(false)
                }
            }
        }

        eventManager.register<RenderEvent> {
            if (guiOpened.get()) {
                ContainerRenderer.render(mainGui)
            }
        }

        eventManager.register { event: KeyEvent ->
            if (event.isPressed && !event.isRepeat && event.key === Key.U) {
                initializeUserInterface()
            }
        }
    }

    private fun addSettingsList(container: Container, settings: List<ConfigurableData?>) {
        for (setting in settings) {
            container.addChild(getSetting(setting))
        }
    }

    private fun getSetting(setting: ConfigurableData?): Component {
        when (setting) {
            is Toggle -> {
                return Container()
                    .apply2 {
                        height = 15.0.toAbsolute()

                        onUpdate += { state ->
                            state["hidden"] = false
                        }

                        children += Container()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                width = 0.05.toRelative()
                            }

                        children += Text()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                text = setting.displayName.toAbsolute()
                                scale = 0.0025.toRelative()
                            }

                        children += Container()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                width = 0.05.toRelative()
                            }

                        children += Container()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                width = 2.0.toCopy()
                                height = 0.6.toRelative()
                                padding = Relative(0.2, true)

                                backgroundCornerRadius = 0.01.toRelative()
                                backgroundColor = { _: Map<String, Any> ->
                                    val toggled = setting.setting.value
                                    if (toggled) Color.fromRGB(20, 118, 188, 255) else Color.fromRGB(20, 118, 188, 125)
                                }.toDependent()

                                onClick = { state ->
                                    if (!setting.setting.isForcedValue) {
                                        val toggled = !(state["toggled"] as Boolean)
                                        state["toggled"] = toggled
                                        setting.setting.setValueRaw(toggled)
                                    }
                                }

                                children += Container()
                                    .apply2 {
                                        x = { _: Map<String, Any> ->
                                            Side(if (setting.setting.value) 1 else -1)
                                        }.toDependent()
                                        width = Copy()
                                        height = 0.8.toRelative()
                                        padding = Relative(0.1, true)

                                        backgroundCornerRadius = 0.1.toRelative()
                                        backgroundColor = Color.WHITE.toAbsolute()
                                    }
                            }

                        storedState += "toggled"
                        runtime.setState("toggled", setting.setting.value)
                    }
            }
            is Slider -> {
                return Container()
                    .apply2 {
                        height = 15.0.toAbsolute()
                        onUpdate += { state ->
                            state["hidden"] = false
                        }

                        children += Container()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                width = 0.05.toRelative()
                            }

                        children += Text()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                text = setting.displayName.toAbsolute()
                                scale = 0.0025.toRelative()
                            }

                        children += Container()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                width = 0.05.toRelative()
                            }

                        children += Container()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                width = 35.0.toCopy()
                                height = 0.1.toRelative()
                                padding = Relative(0.2, true)

                                backgroundColor = Color.fromRGB(255, 0, 0, 255).toAbsolute()
                                backgroundCornerRadius = Relative(0.05, true)

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
                                    }
                                }

                                children += Container()
                                    .apply2 {
                                        x = Side.NEGATIVE.toSide()
                                        width = { _: Map<String, Any> ->
                                            val minimum = setting.minimum
                                            val maximum = setting.maximum
                                            Relative((setting.setting.value.toDouble() - minimum) / (maximum - minimum))
                                        }.toDependent()

                                        backgroundColor = Color.fromRGB(20, 118, 188, 255).toAbsolute()
                                        backgroundCornerRadius = Relative(0.5, true)
                                    }

                                children += Container()
                                    .apply2 {
                                        x = { _: Map<String, Any> ->
                                            val minimum = setting.minimum
                                            val maximum = setting.maximum
                                            Relative((setting.setting.value.toDouble() - minimum) / (maximum - minimum) - 0.5)
                                        }.toDependent()
                                        y = 0.0.toRelative()
                                        width = Copy()
                                        height = 2.0.toRelative()

                                        backgroundColor = Color.WHITE.toAbsolute()
                                        backgroundCornerRadius = Relative(1.0, true)
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
                    .apply2 {
                        height = 15.0.toAbsolute()
                        onUpdate += { state ->
                            state["hidden"] = false
                        }

                        children += Container()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                width = 0.05.toRelative()
                            }

                        children += Text()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                text = setting.displayName.toAbsolute()
                                scale = 0.0025.toRelative()
                            }

                        children += Container()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                width = 0.05.toRelative()
                            }

                        children += Container()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                width = 5.0.toCopy()
                                height = 0.6.toRelative()
                                padding = Relative(0.2, true)

                                backgroundColor = Color.fromRGB(20, 118, 188, 255).toAbsolute()

                                onKey = { state ->
                                    state.first["value"] = state.second
                                    setting.setting.setValueRaw(state.second)
                                    state.first["selected"] = false
                                }

                                children += Text()
                                    .apply2 {
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
                    .apply2 {
                        height = 15.0.toAbsolute()
                        onUpdate += { state ->
                            state["hidden"] = false
                        }

                        children += Container()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                width = 0.05.toRelative()
                            }

                        children += Text()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                text = setting.displayName.toAbsolute()
                                scale = 0.0025.toRelative()
                            }

                        children += Container()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                width = 0.05.toRelative()
                            }

                        children += Container()
                            .apply2 {
                                x = Side.NEGATIVE.toSide()
                                width = 5.0.toCopy()
                                height = 0.6.toRelative()
                                padding = Relative(0.2, true)

                                backgroundColor = Color.fromRGB(20, 118, 188, 255).toAbsolute()

                                children += Container()
                                    .apply2 {
                                        x = Side.NEGATIVE.toSide()
                                        width = Copy()
                                        height = 0.6.toRelative()
                                        padding = Relative(0.15, true)

                                        backgroundImage = "assets/minecraft/arrow_left.png".toAbsolute()

                                        onClick = { state ->
                                            val newValue = 0.coerceAtLeast(state["value"] as Int - 1)
                                            state["value"] = newValue
                                        }
                                    }

                                children += Container()
                                    .apply2 {
                                        x = Side.POSITIVE.toSide()
                                        width = Copy()
                                        height = 0.6.toRelative()
                                        padding = Relative(0.15, true)

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
                                            if (state["value"] as Int + 1 >= valuesLength) {
                                                state["value"] = 0
                                            } else {
                                                state["value"] = state["value"] as Int + 1
                                            }
                                        }
                                    }

                                children += Text()
                                    .apply2 {
                                        fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                        text = { _: Map<String, Any> ->
                                            setting.setting.value.toString()
                                        }.toDependent()
                                        scale = 0.01.toRelative()
                                        textColor = Color.WHITE.toAbsolute()
                                    }

                                onStateUpdate["value"] = { state ->
                                    val values = setting.setting.type.getDeclaredMethod("values").invoke(null) as Array<*>
                                    val setting1: Setting<*> = setting.setting
                                    if (setting1.isForcedValue) {
                                        var index = state["value"] as Int
                                        while (index != -1) {
                                            if (setting1.forcedValues!!.contains(values[index])) {
                                                setting1.setValueRaw(values[index]!!)
                                                state["value"] = index
                                                index = -1
                                            } else {
                                                index++
                                                if (index >= values.size) {
                                                    index = 0
                                                }
                                            }
                                        }
                                    } else {
                                        if (state["value"] != null) {
                                            state["value"].let { setting1.setValueRaw(values[it as Int]!!) }
                                        }
                                    }
                                }
                            }

                        storedState += "value"
                        runtime.setState("value", setting.setting.value.ordinal)
                    }
            }
            is ColorPicker -> {
                return TabHolder()
                    .apply2 {
                        addChild("edit", Container()
                            .apply2 {
                                onUpdate += { state ->
                                    state["hidden"] = false
                                }

                                children += Container()
                                    .apply2 {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.05.toRelative()
                                    }

                                children += Text()
                                    .apply2 {
                                        x = Side.NEGATIVE.toSide()
                                        fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                        text = setting.displayName.toAbsolute()
                                        scale = 0.0025.toRelative()
                                    }

                                children += Container()
                                    .apply2 {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.05.toRelative()
                                    }

                                children += Container()
                                    .apply2 {
                                        x = Side.NEGATIVE.toSide()
                                        width = Copy()
                                        height = 0.85.toRelative()
                                        padding = Relative(0.1, true)

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
                                            .apply2 {
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
                                    }

                                children += Container()
                                    .apply2 {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.25.toCopy()
                                        height = 0.85.toRelative()
                                        padding = Relative(0.1, true)

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
                                            .apply2 {
                                                x = 0.0.toRelative()
                                                y = { state: Map<String, Any> ->
                                                    Relative((state["value"] as FloatArray)[0].toDouble() - 0.5)
                                                }.toDependent()

                                                width = 1.0.toRelative()
                                                height = 0.5.toAbsolute()

                                                backgroundColor = Color.WHITE.toAbsolute()
                                            }
                                    }

                                children += Container()
                                    .apply2 {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.25.toCopy()
                                        height = 0.85.toRelative()
                                        padding = Relative(0.1, true)

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
                                            .apply2 {
                                                x = 0.0.toRelative()
                                                y = { state: Map<String, Any> ->
                                                    Relative((1 - (state["value"] as FloatArray)[3]).toDouble() - 0.5)
                                                }.toDependent()

                                                width = 1.0.toRelative()
                                                height = 0.5.toAbsolute()

                                                backgroundColor = Color.WHITE.toAbsolute()
                                            }
                                    }
                            })

                        addChild("view", Container()
                            .apply2 {
                                onUpdate += { state ->
                                    state["hidden"] = false
                                }

                                children += Container()
                                    .apply2 {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.05.toRelative()
                                    }

                                children += Text()
                                    .apply2 {
                                        x = Side.NEGATIVE.toSide()
                                        fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                        text = setting.displayName.toAbsolute()
                                        scale = 0.0025.toRelative()
                                    }

                                children += Container()
                                    .apply2 {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.05.toRelative()
                                    }

                                children += Container()
                                    .apply2 {
                                        x = Side.NEGATIVE.toSide()
                                        width = Copy()
                                        height = 0.6.toRelative()

                                        backgroundColor = { _: Map<String, Any> ->
                                            setting.setting.value
                                        }.toDependent()
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
                        storedState += "colorTab"
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

                        onInit = { state ->
                            state.second["colorTab"] = "view"
                        }

                        height = { state: Map<String, Any> ->
                            Absolute(if (state["colorTab"] == "edit") 30.0 else 15.0)
                        }.toDependent()
                    }
            }
            is ConfigurableData.Dependent<*> -> {
                return (getSetting(setting.configurableData) as Container)
                    .apply2 {
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
        mainGui = TabHolder()
            .apply2 {
                stateId = "currentTab"

                val tabs = arrayOf("home", "hudEdit", "moduleEdit", "pluginEdit", "profileEdit")
                for (tab in tabs) {
                    val container1 = Container()
                    addNavBar(container1, tabs)
                    if (tab == "moduleEdit") {
                        addModulesScreen(container1)
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
            Container().apply2 {
                    y = Side.POSITIVE.toSide()
                    width = 0.53.toRelative()
                    height = 0.09.toRelative()
                    padding = 0.01.toRelative()

                    backgroundCornerRadius = 0.0075.toRelative()
                    backgroundColor = Color.fromRGB(26, 26, 26, 230).toAbsolute()

                    children += Container().apply2 {
                        x = Side.NEGATIVE.toSide()
                        width = Copy()
                        height = 0.7.toRelative()
                        padding = 0.02.toRelative()
                        backgroundImage = "assets/minecraft/sorus.png".toAbsolute()
                    }

                    children += List(com.github.sorusclient.client.ui.framework.List.HORIZONTAL).apply2 {
                        x = Side.ZERO.toSide()
                        y = Side.ZERO.toSide()
                        height = 0.7.toRelative()

                        for (tab in tabs) {
                            children += Container().apply2 {
                                width = Copy()
                                padding = 0.01.toRelative()
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

                                children += Container().apply2 {
                                    width = 0.5.toRelative()
                                    height = 0.5.toRelative()
                                    backgroundImage = "assets/minecraft/$tab.png".toAbsolute()
                                }
                            }

                            children += Container().apply2 {
                                width = 0.1.toCopy()
                            }
                        }
                    }
                })
    }

    private fun addModulesScreen(container: Container) {
        container.apply2 {
            children += TabHolder().apply2 {
                addChild("main", Container()
                    .apply2 {
                        y = Side.NEGATIVE.toSide()
                        width = 0.53.toRelative()
                        padding = 0.01.toRelative()
                        backgroundCornerRadius = 0.0075.toRelative()
                        backgroundColor = Color.fromRGB(26, 26, 26, 230).toAbsolute()

                        children += Container()
                            .apply2 {
                                y = Side.NEGATIVE.toSide()
                                height = 0.05.toCopy()
                                padding = 0.005.toRelative()

                                children += Text()
                                    .apply2 {
                                        x = Side.NEGATIVE.toSide()
                                        fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                        padding = 0.01.toRelative()
                                        text = "Module".toAbsolute()
                                        scale = 0.003.toRelative()
                                    }
                            }

                        children += Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                            .apply2 {
                                for (module in ModuleManager.modules.values) {
                                    children += Container()
                                        .apply2 {
                                            height = 0.1.toCopy()
                                            padding = 0.01.toRelative()
                                            backgroundCornerRadius = 0.01.toRelative()
                                            backgroundColor = Color.fromRGB(24, 24, 24, 255).toAbsolute()

                                            children += Container()
                                                .apply2 {
                                                    x = Side.NEGATIVE.toSide()
                                                    width = Copy()
                                                    height = 0.6.toRelative()
                                                    padding = Relative(0.2, true)
                                                    backgroundCornerRadius = 0.01.toRelative()
                                                    backgroundColor = Color.fromRGB(255, 255, 255, 200).toAbsolute()
                                                }

                                            children += Text()
                                                .apply2 {
                                                    x = Side.NEGATIVE.toSide()
                                                    y = Side.NEGATIVE.toSide()
                                                    padding = Relative(0.15, true)

                                                    text = module.name.toAbsolute()
                                                    fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                                    scale = 0.003.toRelative()
                                                }

                                            children += Text()
                                                .apply2 {
                                                    x = Side.NEGATIVE.toSide()
                                                    y = Side.POSITIVE.toSide()
                                                    padding = Relative(0.15, true)

                                                    text = module.description.toAbsolute()
                                                    fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                                    scale = 0.0025.toRelative()
                                                    textColor = Color.fromRGB(255, 255, 255, 80).toAbsolute()
                                                }

                                            children += Container()
                                                .apply2 {
                                                    x = Side.POSITIVE.toSide()
                                                    y = Side.ZERO.toSide()
                                                    width = Copy()
                                                    height = 0.4.toRelative()
                                                    padding = Relative(0.3, true)

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
                    .apply2 {
                        y = Side.NEGATIVE.toSide()
                        width = 0.53.toRelative()
                        padding = 0.01.toRelative()
                        backgroundCornerRadius = 0.0075.toRelative()
                        backgroundColor = Color.fromRGB(26, 26, 26, 230).toAbsolute()

                        onInit = { state ->
                            val moduleData = state.second["currentEditingModule"] as ModuleData
                            val settings: MutableList<ConfigurableData> = ArrayList()

                            moduleData.module.addSettings(settings)

                            state.first.apply2 {
                                clear()

                                children += Container()
                                    .apply2 {
                                        y = Side.NEGATIVE.toSide()
                                        height = 0.04.toCopy()
                                        padding = 0.005.toRelative()

                                        children += Text()
                                            .apply2 {
                                                x = Side.NEGATIVE.toSide()
                                                padding = 0.01.toRelative()

                                                fontRenderer = "Quicksand-Medium.ttf".toAbsolute()
                                                text = moduleData.name.toAbsolute()
                                                scale = 0.003.toRelative()
                                            }
                                    }

                                children += Container()
                                    .apply2 {
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
                                    .apply2 {
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
                .apply2 {
                    x = Side.NEGATIVE.toSide()
                    padding = 0.01.toRelative()
                    backgroundCornerRadius = 0.0075.toRelative()
                    backgroundColor = Color.fromRGB(26, 26, 26, 230).toAbsolute()
                }
        }
    }

    fun isHudEditScreenOpen(): Boolean {
        return hudEditScreenOpen.get() && guiOpened.get()
    }
}
