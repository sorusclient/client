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
import com.github.sorusclient.client.ui.framework.constraint.Dependent
import com.github.sorusclient.client.util.Color
import com.github.sorusclient.client.util.Pair
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Function
import kotlin.collections.List
import kotlin.collections.set

object UserInterface {
    private val hudEditScreenOpen = AtomicBoolean(false)
    private var mainGui: Container? = null
    private val guiOpened = AtomicBoolean(false)
    fun initialize() {
        initializeUserInterface()
        val eventManager = EventManager
        eventManager.register(KeyEvent::class.java) { event: KeyEvent ->
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
        eventManager.register(RenderEvent::class.java) {
            if (guiOpened.get()) {
                ContainerRenderer.render(mainGui)
            }
        }
        eventManager.register(KeyEvent::class.java) { event: KeyEvent ->
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
                    .addOnUpdate { state: MutableMap<String, Any> -> state["hidden"] = false }
                    .setHeight(Absolute(15.0))
                    .addChild(
                        Container()
                            .setWidth(Relative(0.05))
                            .setX(Side(Side.NEGATIVE))
                    )
                    .addChild(
                        Text()
                            .setFontRenderer(Absolute("minecraft"))
                            .setText(Absolute(setting.displayName))
                            .setScale(Relative(0.0025))
                            .setX(Side(Side.NEGATIVE))
                    )
                    .addChild(
                        Container()
                            .setWidth(Relative(0.05))
                            .setX(Side(Side.NEGATIVE))
                    )
                    .addChild(Container()
                        .setX(Side(Side.NEGATIVE))
                        .setWidth(Copy(2.0))
                        .setHeight(Relative(0.6))
                        .setBackgroundCornerRadius(Relative(0.01))
                        .setPadding(Relative(0.2, true))
                        .setBackgroundColor(Dependent {
                            val toggled = setting.setting.value
                            if (toggled) Color.fromRGB(20, 118, 188, 255) else Color.fromRGB(20, 118, 188, 125)
                        })
                        .setOnClick { state1: MutableMap<String, Any> ->
                            if (!setting.setting.isForcedValue) {
                                val toggled = !(state1["toggled"] as Boolean)
                                state1["toggled"] = toggled
                                setting.setting.setValueRaw(toggled)
                            }
                        }
                        .addChild(
                            Container()
                                .setWidth(Copy())
                                .setHeight(Relative(0.8))
                                .setPadding(Relative(0.1, true))
                                .setX(Dependent { Side(if (setting.setting.value) 1 else -1) })
                                .setBackgroundColor(Color.WHITE)
                                .setBackgroundCornerRadius(Relative(0.1))
                        )
                    )
                    .apply { container2: Container ->
                        container2.addStoredState("toggled")
                        container2.runtime.setState("toggled", setting.setting.value)
                    }
            }
            is Slider -> {
                return Container()
                    .addOnUpdate { state: MutableMap<String, Any> -> state["hidden"] = false }
                    .setHeight(Absolute(15.0))
                    .addChild(
                        Container()
                            .setWidth(Relative(0.05))
                            .setX(Side(Side.NEGATIVE))
                    )
                    .addChild(
                        Text()
                            .setFontRenderer(Absolute("minecraft"))
                            .setText(Absolute(setting.displayName))
                            .setScale(Relative(0.0025))
                            .setX(Side(Side.NEGATIVE))
                    )
                    .addChild(
                        Container()
                            .setWidth(Relative(0.05))
                            .setX(Side(Side.NEGATIVE))
                    )
                    .addChild(Container()
                        .setX(Side(Side.NEGATIVE))
                        .setWidth(Copy(35.0))
                        .setHeight(Relative(0.1))
                        .setPadding(Relative(0.2, true))
                        .setBackgroundColor(Absolute(Color.fromRGB(255, 255, 255, 155)))
                        .setBackgroundCornerRadius(Relative(0.05, true))
                        .setOnDrag { state1: Pair<MutableMap<String, Any>, Pair<Double, Double>> ->
                            if (!setting.setting.isForcedValue) {
                                val minimum = setting.minimum
                                val maximum = setting.maximum
                                val value = state1.second.first
                                state1.first["value"] = value
                                val actualSetting: Setting<*> = setting.setting
                                val valueToSet = (maximum - minimum) * value + minimum
                                if (actualSetting.type == Double::class.java) {
                                    actualSetting.setValueRaw(valueToSet)
                                } else if (actualSetting.type == Long::class.java) {
                                    actualSetting.setValueRaw(valueToSet.toLong())
                                }
                            }
                        }
                        .addChild(
                            Container()
                                .setX(Side(Side.NEGATIVE))
                                .setWidth(Dependent {
                                    val minimum = setting.minimum
                                    val maximum = setting.maximum
                                    Relative((setting.setting.value.toDouble() - minimum) / (maximum - minimum))
                                })
                                .setBackgroundColor(Color.fromRGB(20, 118, 188, 255))
                                .setBackgroundCornerRadius(Relative(0.5, true))
                        )
                        .addChild(
                            Container()
                                .setWidth(Copy())
                                .setHeight(Relative(2.0))
                                .setX(Dependent {
                                    val minimum = setting.minimum
                                    val maximum = setting.maximum
                                    Relative((setting.setting.value.toDouble() - minimum) / (maximum - minimum) - 0.5)
                                })
                                .setY(Relative(0.0))
                                .setBackgroundColor(Color.WHITE)
                                .setBackgroundCornerRadius(Relative(1.0, true))
                        )
                    )
                    .apply { container2: Container ->
                        container2.addStoredState("value")
                        val minimum = setting.minimum
                        val maximum = setting.maximum
                        container2.runtime.setState(
                            "value",
                            (setting.setting.value.toDouble() - minimum) / (maximum - minimum)
                        )
                    }
            }
            is KeyBind -> {
                return Container()
                    .addOnUpdate { state: MutableMap<String, Any> -> state["hidden"] = false }
                    .setHeight(Absolute(15.0))
                    .addChild(
                        Container()
                            .setWidth(Relative(0.05))
                            .setX(Side(Side.NEGATIVE))
                    )
                    .addChild(
                        Text()
                            .setFontRenderer(Absolute("minecraft"))
                            .setText(Absolute(setting.displayName))
                            .setScale(Relative(0.0025))
                            .setX(Side(Side.NEGATIVE))
                    )
                    .addChild(
                        Container()
                            .setWidth(Relative(0.05))
                            .setX(Side(Side.NEGATIVE))
                    )
                    .addChild(Container()
                        .setX(Side(Side.NEGATIVE))
                        .setWidth(Copy(5.0))
                        .setHeight(Relative(0.6))
                        .setPadding(Relative(0.2, true))
                        .setBackgroundColor(Absolute(Color.fromRGB(20, 118, 188, 255)))
                        .setOnKey { state1: Pair<MutableMap<String, Any>, Key> ->
                            state1.first["value"] = state1.second
                            setting.setting.setValueRaw(state1.second)
                            state1.first["selected"] = false
                        }
                        .addChild(
                            Text()
                                .setText(Dependent(Function { state1: Map<String, Any> ->
                                    return@Function if (state1["selected"] as Boolean) {
                                        "..."
                                    } else {
                                        val key = state1["value"] as Key?
                                        key.toString()
                                    }
                                }))
                                .setFontRenderer(Absolute("minecraft"))
                                .setScale(Relative(0.01))
                        )
                    )
                    .apply { container2: Container ->
                        container2.addStoredState("value")
                        container2.addStoredState("selected2")
                        container2.runtime.setState("value", setting.setting.value)
                    }
                    .addOnStateUpdate("selected") { state: MutableMap<String, Any> ->
                        state["selected2"] = state["selected"] as Any
                    }
            }
            is ClickThrough -> {
                return Container()
                    .addOnUpdate { state: MutableMap<String, Any> -> state["hidden"] = false }
                    .setHeight(Absolute(15.0))
                    .addChild(
                        Container()
                            .setWidth(Relative(0.05))
                            .setX(Side(Side.NEGATIVE))
                    )
                    .addChild(
                        Text()
                            .setFontRenderer(Absolute("minecraft"))
                            .setText(Absolute(setting.displayName))
                            .setScale(Relative(0.0025))
                            .setX(Side(Side.NEGATIVE))
                    )
                    .addChild(
                        Container()
                            .setWidth(Relative(0.05))
                            .setX(Side(Side.NEGATIVE))
                    )
                    .addChild(
                        Container()
                            .setX(Side(Side.NEGATIVE))
                            .setWidth(Copy(5.0))
                            .setHeight(Relative(0.6))
                            .setPadding(Relative(0.2, true))
                            .setBackgroundColor(Absolute(Color.fromRGB(20, 118, 188, 255)))
                            .addChild(
                                Container()
                                    .setX(Side(Side.NEGATIVE))
                                    .setWidth(Copy())
                                    .setHeight(Relative(0.6))
                                    .setPadding(Relative(0.15, true))
                                    .setBackgroundImage(Absolute("arrow_left.png"))
                                    .setOnClick { state1: MutableMap<String, Any> ->
                                        val newValue = 0.coerceAtLeast(state1["value"] as Int - 1)
                                        state1["value"] = newValue
                                    })
                            .addChild(
                                Container()
                                    .setX(Side(Side.POSITIVE))
                                    .setWidth(Copy())
                                    .setHeight(Relative(0.6))
                                    .setPadding(Relative(0.15, true))
                                    .setBackgroundImage(Absolute("arrow_right.png"))
                                    .setOnClick { state1: MutableMap<String, Any> ->
                                        var valuesLength = 0
                                        try {
                                            valuesLength = (setting.setting.type.getDeclaredMethod("values")
                                                .invoke(null) as Array<*>).size
                                        } catch (e: IllegalAccessException) {
                                            e.printStackTrace()
                                        } catch (e: InvocationTargetException) {
                                            e.printStackTrace()
                                        } catch (e: NoSuchMethodException) {
                                            e.printStackTrace()
                                        }
                                        if (state1["value"] as Int + 1 >= valuesLength) {
                                            state1["value"] = 0
                                        } else {
                                            state1["value"] = state1["value"] as Int + 1
                                        }
                                    })
                            .addChild(
                                Text()
                                    .setText(Dependent { setting.setting.value.toString() })
                                    .setFontRenderer(Absolute("minecraft"))
                                    .setScale(Relative(0.01))
                                    .setTextColor(Absolute(Color.WHITE))
                            )
                            .addOnStateUpdate("value") { state1: MutableMap<String, Any> ->
                                try {
                                    val values =
                                        setting.setting.type.getDeclaredMethod("values").invoke(null) as Array<*>
                                    val setting1: Setting<*> = setting.setting
                                    if (setting1.isForcedValue) {
                                        var index = state1["value"] as Int
                                        while (index != -1) {
                                            if (setting1.forcedValues!!.contains(values[index])) {
                                                setting1.setValueRaw(values[index]!!)
                                                state1["value"] = index
                                                index = -1
                                            } else {
                                                index++
                                                if (index >= values.size) {
                                                    index = 0
                                                }
                                            }
                                        }
                                    } else {
                                        setting1.setValueRaw(values[state1["value"] as Int]!!)
                                    }
                                } catch (e: IllegalAccessException) {
                                    e.printStackTrace()
                                } catch (e: InvocationTargetException) {
                                    e.printStackTrace()
                                } catch (e: NoSuchMethodException) {
                                    e.printStackTrace()
                                }
                            })
                    .apply { container2: Container ->
                        container2.addStoredState("value")
                        container2.runtime.setState("value", setting.setting.value.ordinal)
                    }
            }
            is ColorPicker -> {
                return TabHolder()
                    .addChild("edit", Container()
                        .addOnUpdate { state: MutableMap<String, Any> -> state["hidden"] = false }
                        .addChild(
                            Container()
                                .setWidth(Relative(0.05))
                                .setX(Side(Side.NEGATIVE))
                        )
                        .addChild(
                            Text()
                                .setFontRenderer(Absolute("minecraft"))
                                .setText(Absolute(setting.displayName))
                                .setScale(Relative(0.0025))
                                .setX(Side(Side.NEGATIVE))
                        )
                        .addChild(
                            Container()
                                .setWidth(Relative(0.05))
                                .setX(Side(Side.NEGATIVE))
                        )
                        .addChild(Container()
                            .setX(Side(Side.NEGATIVE))
                            .setWidth(Copy())
                            .setHeight(Relative(0.85))
                            .setPadding(Relative(0.1, true))
                            .setTopLeftBackgroundColor(Absolute(Color.WHITE))
                            .setBottomLeftBackgroundColor(Absolute(Color.BLACK))
                            .setBottomRightBackgroundColor(Absolute(Color.BLACK))
                            .setTopRightBackgroundColor(Dependent { state1: Map<String, Any> ->
                                val colorData = state1["value"] as FloatArray?
                                val rgb = java.awt.Color.HSBtoRGB(colorData!![0], 1f, 1f)
                                val javaColor = java.awt.Color(rgb)
                                Color.fromRGB(javaColor.red, javaColor.green, javaColor.blue, 255)
                            })
                            .setOnDrag { state1: Pair<MutableMap<String, Any>, Pair<Double, Double>> ->
                                val colorData = state1.first["value"] as FloatArray?
                                val colorDataNew = floatArrayOf(
                                    colorData!![0],
                                    state1.second.first.toFloat(),
                                    1 - state1.second.second.toFloat(),
                                    colorData[3]
                                )
                                state1.first["value"] = colorDataNew
                            }
                            .addChild(
                                Container()
                                    .setX(Dependent { state: Map<String, Any> ->
                                        val colorData = state["value"] as FloatArray
                                        Relative(colorData[1] - 0.5)
                                    })
                                    .setY(Dependent { state: Map<String, Any> ->
                                        val colorData = state["value"] as FloatArray
                                        Relative(1 - colorData[2] - 0.5)
                                    })
                                    .setWidth(Absolute(1.5))
                                    .setHeight(Copy())
                                    .setBackgroundCornerRadius(Absolute(0.75))
                                    .setBackgroundColor(Color.WHITE)
                            )
                        )
                        .addChild(Container()
                            .setX(Side(Side.NEGATIVE))
                            .setWidth(Copy(0.25))
                            .setHeight(Relative(0.85))
                            .setPadding(Relative(0.1, true))
                            .setBackgroundImage(Absolute("color_range.png"))
                            .setOnDrag { state1: Pair<MutableMap<String, Any>, Pair<Double, Double>> ->
                                val colorData = state1.first["value"] as FloatArray
                                val colorDataNew = floatArrayOf(
                                    state1.second.second.toFloat(),
                                    colorData[1],
                                    colorData[2],
                                    colorData[3]
                                )
                                state1.first["value"] = colorDataNew
                            }
                            .addChild(
                                Container()
                                    .setX(Relative(0.0))
                                    .setY(Dependent { state1: Map<String, Any> ->
                                        Relative(
                                            (state1["value"] as FloatArray)[0].toDouble() - 0.5
                                        )
                                    })
                                    .setWidth(Relative(1.0))
                                    .setHeight(Absolute(0.5))
                                    .setBackgroundColor(Absolute(Color.WHITE))
                            )
                        )
                        .addChild(Container()
                            .setX(Side(Side.NEGATIVE))
                            .setWidth(Copy(0.25))
                            .setHeight(Relative(0.85))
                            .setPadding(Relative(0.1, true))
                            .setTopRightBackgroundColor(Absolute(Color.WHITE))
                            .setTopLeftBackgroundColor(Absolute(Color.WHITE))
                            .setBottomRightBackgroundColor(Absolute(Color.fromRGB(255, 255, 255, 50)))
                            .setBottomLeftBackgroundColor(Absolute(Color.fromRGB(255, 255, 255, 50)))
                            .setOnDrag { state1: Pair<MutableMap<String, Any>, Pair<Double, Double>> ->
                                val colorData = state1.first["value"] as FloatArray
                                val colorDataNew = floatArrayOf(
                                    colorData[0],
                                    colorData[1],
                                    colorData[2],
                                    1 - state1.second.second.toFloat()
                                )
                                state1.first["value"] = colorDataNew
                            }
                            .addChild(
                                Container()
                                    .setX(Relative(0.0))
                                    .setY(Dependent { state1: Map<String, Any> -> Relative((1 - (state1["value"] as FloatArray)[3]).toDouble() - 0.5) })
                                    .setWidth(Relative(1.0))
                                    .setHeight(Absolute(0.5))
                                    .setBackgroundColor(Absolute(Color.WHITE))
                            )
                        )
                        .apply { container2: Container ->
                            container2.addStoredState("value")
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
                            container2.runtime.setState("value", colorData)
                        }
                        .addOnStateUpdate("value") { state1: Map<String, Any> ->
                            val stateValue = state1["value"] as FloatArray
                            val rgb = java.awt.Color.HSBtoRGB(stateValue[0], stateValue[1], stateValue[2])
                            val javaColor = java.awt.Color(rgb)
                            setting.setting.realValue = Color.fromRGB(
                                javaColor.red,
                                javaColor.green,
                                javaColor.blue,
                                (stateValue[3] * 255).toInt()
                            )
                        })
                    .addChild("view", Container()
                        .addOnUpdate { state: MutableMap<String, Any> -> state["hidden"] = false }
                        .addChild(
                            Container()
                                .setWidth(Relative(0.05))
                                .setX(Side(Side.NEGATIVE))
                        )
                        .addChild(
                            Text()
                                .setFontRenderer(Absolute("minecraft"))
                                .setText(Absolute(setting.displayName))
                                .setScale(Relative(0.0025))
                                .setX(Side(Side.NEGATIVE))
                        )
                        .addChild(
                            Container()
                                .setWidth(Relative(0.05))
                                .setX(Side(Side.NEGATIVE))
                        )
                        .addChild(Container()
                            .setX(Side(Side.NEGATIVE))
                            .setWidth(Copy())
                            .setHeight(Relative(0.6))
                            .setBackgroundColor(Dependent { setting.setting.value })
                        )
                    )
                    .setStateId("colorTab")
                    .setOnInit { state: Pair<Container, MutableMap<String, Any>> ->
                        state.second["colorTab"] = "view"
                    }
                    .setHeight(Dependent { state: Map<String, Any> -> Absolute(if (state["colorTab"] == "edit") 30.0 else 15.0) })
                    .addStoredState("colorTab")
                    .addOnStateUpdate("selected") { state: MutableMap<String, Any> ->
                        state["colorTab"] = if (state["selected"] as Boolean) "edit" else "view"
                    }
            }
            is ConfigurableData.Dependent<*> -> {
                return (getSetting(setting.configurableData) as Container)
                    .addOnUpdate { state: MutableMap<String, Any> ->
                        if (setting.setting.value != setting.expectedValue) {
                            state["hidden"] = true
                        }
                    }
            }
            else -> return null!!
        }
    }

    private fun initializeUserInterface() {
        mainGui = TabHolder()
            .setStateId("currentTab")
            .apply { container: Container ->
                val tabs = arrayOf("home", "hudEdit", "moduleEdit", "pluginEdit", "profileEdit")
                val tabHolder = container as TabHolder?
                for (tab in tabs) {
                    val container1 = Container()
                    addNavBar(container1, tabs)
                    if (tab == "moduleEdit") {
                        addModulesScreen(container1)
                    }
                    tabHolder!!.addChild(tab, container1)
                }
            }
            .setOnInit { state: Pair<Container, MutableMap<String, Any>> ->
                state.first.addStoredState("currentTab")
                state.second["currentTab"] = "home"
            }
            .addOnStateUpdate("currentTab") { state: Map<String, Any> ->
                hudEditScreenOpen.set(
                    state["currentTab"] == "hudEdit"
                )
            } as Container
    }

    private fun addNavBar(container: Container, tabs: Array<String>) {
        container.addChild(
            Container()
                .setY(Side(Side.POSITIVE))
                .setWidth(Relative(0.53))
                .setHeight(Relative(0.09))
                .setPadding(Relative(0.01))
                .setBackgroundCornerRadius(Relative(0.0075))
                .setBackgroundColor(Color.fromRGB(26, 26, 26, 230))
                .addChild(
                    Container()
                        .setX(Side(Side.NEGATIVE))
                        .setWidth(Copy())
                        .setHeight(Relative(0.7))
                        .setPadding(Relative(0.02))
                        .setBackgroundImage(Absolute("sorus.png"))
                )
                .addChild(
                    List(com.github.sorusclient.client.ui.framework.List.HORIZONTAL)
                        .setX(Side(Side.ZERO))
                        .setY(Side(Side.ZERO))
                        .setHeight(Relative(0.7))
                        .apply { list: Container ->
                            for (tab in tabs) {
                                list.addChild(Container()
                                    .setWidth(Copy())
                                    .setPadding(Relative(0.01))
                                    .setBackgroundCornerRadius(Relative(0.0075))
                                    .setOnClick { state: MutableMap<String, Any> -> state["currentTab"] = tab }
                                    .setBackgroundColor(Dependent(Function { state: Map<String, Any> ->
                                        return@Function if (state["currentTab"] == tab) {
                                            Color.fromRGB(20, 118, 188, 255)
                                        } else {
                                            Color.fromRGB(24, 24, 24, 255)
                                        }
                                    }))
                                    .addChild(
                                        Container()
                                            .setWidth(Relative(0.5))
                                            .setHeight(Relative(0.5))
                                            .setBackgroundImage(Absolute("$tab.png"))
                                    )
                                )
                                    .addChild(
                                        Container()
                                            .setWidth(Copy(0.1))
                                    )
                            }
                        })
        )
    }

    private fun addModulesScreen(container: Container) {
        container.addChild(TabHolder()
            .addChild("main", Container()
                .setY(Side(Side.NEGATIVE))
                .setWidth(Relative(0.53))
                .setPadding(Relative(0.01))
                .setBackgroundCornerRadius(Relative(0.0075))
                .setBackgroundColor(Color.fromRGB(26, 26, 26, 230))
                .addChild(
                    Container()
                        .setY(Side(Side.NEGATIVE))
                        .setHeight(Copy(0.04))
                        .setPadding(Relative(0.005))
                        .addChild(
                            Text()
                                .setFontRenderer(Absolute("minecraft"))
                                .setPadding(Relative(0.01))
                                .setText(Absolute("Modules"))
                                .setScale(Relative(0.003))
                                .setX(Side(Side.NEGATIVE))
                        )
                )
                .addChild(Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                    .apply { container1: Container ->
                        for (module in ModuleManager.modules.values) {
                            container1.addChild(
                                Container()
                                    .setHeight(Copy(0.1))
                                    .setPadding(Relative(0.01))
                                    .setBackgroundCornerRadius(Relative(0.01))
                                    .setBackgroundColor(Color.fromRGB(24, 24, 24, 255))
                                    .addChild(
                                        Container()
                                            .setX(Side(Side.NEGATIVE))
                                            .setWidth(Copy())
                                            .setHeight(Relative(0.6))
                                            .setPadding(Relative(0.2, true))
                                            .setBackgroundCornerRadius(Relative(0.01))
                                            .setBackgroundColor(Color.fromRGB(255, 255, 255, 200))
                                    )
                                    .addChild(
                                        Text()
                                            .setText(Absolute(module.name))
                                            .setFontRenderer(Absolute("minecraft"))
                                            .setScale(Relative(0.003))
                                            .setPadding(Relative(0.175, true))
                                            .setX(Side(Side.NEGATIVE))
                                            .setY(Side(Side.NEGATIVE))
                                    )
                                    .addChild(
                                        Text()
                                            .setText(Absolute(module.description))
                                            .setFontRenderer(Absolute("minecraft"))
                                            .setScale(Relative(0.003))
                                            .setTextColor(Absolute(Color.fromRGB(255, 255, 255, 80)))
                                            .setPadding(Relative(0.175, true))
                                            .setX(Side(Side.NEGATIVE))
                                            .setY(Side(Side.POSITIVE))
                                    )
                                    .addChild(
                                        Container()
                                            .setBackgroundImage(Absolute("gear.png"))
                                            .setX(Side(Side.POSITIVE))
                                            .setY(Side(Side.ZERO))
                                            .setWidth(Copy())
                                            .setHeight(Relative(0.4))
                                            .setPadding(Relative(0.3, true))
                                            .setBackgroundColor(Color.fromRGB(255, 255, 255, 75))
                                            .setOnClick { state: MutableMap<String, Any> ->
                                                state["moduleScreen"] = "edit"
                                                state["currentEditingModule"] = module
                                            })
                            )
                        }
                    }
                    .setScissor(true)))
            .addChild("edit", Container()
                .setY(Side(Side.NEGATIVE))
                .setWidth(Relative(0.53))
                .setPadding(Relative(0.01))
                .setBackgroundCornerRadius(Relative(0.0075))
                .setBackgroundColor(Color.fromRGB(26, 26, 26, 230))
                .setOnInit { state: Pair<Container, MutableMap<String, Any>> ->
                    val container1 = state.first
                    val moduleData = state.second["currentEditingModule"] as ModuleData
                    val settings: MutableList<ConfigurableData> = ArrayList()
                    moduleData.module.addSettings(settings)
                    container1
                        .clear()
                        .addChild(
                            Container()
                                .setY(Side(Side.NEGATIVE))
                                .setHeight(Copy(0.04))
                                .setPadding(Relative(0.005))
                                .addChild(
                                    Text()
                                        .setFontRenderer(Absolute("minecraft"))
                                        .setPadding(Relative(0.01))
                                        .setText(Absolute(moduleData.name))
                                        .setScale(Relative(0.003))
                                        .setX(Side(Side.NEGATIVE))
                                )
                        )
                        .addChild(
                            Container()
                                .setX(Side(Side.POSITIVE))
                                .setY(Side(Side.POSITIVE))
                                .setWidth(Absolute(25.0))
                                .setHeight(Absolute(25.0))
                                .setBackgroundColor(Absolute(Color.WHITE))
                                .setOnClick { state1: MutableMap<String, Any> ->
                                    state1["currentModuleTab"] = "main"
                                })
                        .addChild(Scroll(com.github.sorusclient.client.ui.framework.List.VERTICAL)
                            .setScissor(true)
                            .setY(Side(Side.NEGATIVE))
                            .apply { container2: Container -> addSettingsList(container2, settings) })
                })
            .setStateId("moduleScreen")
            .setOnInit { state: Pair<Container, MutableMap<String, Any>> ->
                state.first.addStoredState("moduleScreen")
                state.first.addStoredState("currentEditingModule")
                state.second["moduleScreen"] = "main"
            })
            .addChild(
                Container()
                    .setX(Side(Side.NEGATIVE))
                    .setPadding(Relative(0.01))
                    .setBackgroundCornerRadius(Relative(0.0075))
                    .setBackgroundColor(Color.fromRGB(26, 26, 26, 230))
            )
    }

    fun isHudEditScreenOpen(): Boolean {
        return hudEditScreenOpen.get() && guiOpened.get()
    }
}