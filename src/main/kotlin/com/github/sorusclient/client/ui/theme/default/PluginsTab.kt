/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.ui.theme.default

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.plugin.PluginManager
import com.github.sorusclient.client.ui.*
import com.github.sorusclient.client.ui.framework.Container
import com.github.sorusclient.client.ui.framework.Scroll
import com.github.sorusclient.client.ui.framework.Text
import com.github.sorusclient.client.ui.framework.constraint.Relative
import com.github.sorusclient.client.ui.framework.constraint.Side
import com.github.sorusclient.client.util.AssetUtil
import com.github.sorusclient.client.util.Color
import org.apache.commons.io.FileUtils
import org.json.JSONObject
import java.io.File
import java.net.URL

class PluginsTab(private val defaultTheme: DefaultTheme): Container() {

    init {
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

                backgroundColor = { defaultTheme.backgroundColor.value }.toDependent()
                borderColor = { defaultTheme.borderColor.value }.toDependent()
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
                                    textColor = { defaultTheme.elementColor.value }.toDependent()
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
                                        borderThickness = 0.0025.toRelative()

                                        backgroundColor = { defaultTheme.midgroundColor.value }.toDependent()
                                        borderColor = { defaultTheme.borderColor.value }.toDependent()

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
                                                textColor = { defaultTheme.elementColor.value }.toDependent()
                                            }

                                        children += Text()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                y = Side.POSITIVE.toSide()
                                                setPadding(Relative(0.175, true))

                                                fontRenderer = "sorus/ui/font/Quicksand-Regular.ttf".toAbsolute()
                                                scale = 0.0035.toRelative()
                                                text = plugin.description.toAbsolute()
                                                textColor = { defaultTheme.elementColor.value }.toDependent()
                                            }

                                        children += Container()
                                            .apply {
                                                x = Side.POSITIVE.toSide()
                                                width = 1.0.toCopy()

                                                setPadding(Relative(0.2, true))

                                                backgroundCornerRadius = 0.015.toRelative()
                                                borderThickness = 0.0025.toRelative()

                                                backgroundColor = { defaultTheme.midgroundColor.value }.toDependent()

                                                borderColor = { defaultTheme.borderColor.value }.toDependent()
                                                borderColor = { state: Map<String, Any> ->
                                                    if (state["hovered"] as Boolean) {
                                                        { defaultTheme.selectedBorderColor.value }.toDependent()
                                                    } else {
                                                        Color.fromRGB(10, 10, 10, 150)
                                                    }
                                                }.toDependent()

                                                children += Container()
                                                    .apply {
                                                        width = 0.5.toRelative()
                                                        height = 1.0.toCopy()

                                                        backgroundImage = "sorus/ui/profiles/delete.png".toAbsolute()
                                                        backgroundColor = { defaultTheme.elementColor.value }.toDependent()
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

                backgroundColor = { defaultTheme.backgroundColor.value }.toDependent()
                borderColor = { defaultTheme.borderColor.value }.toDependent()
                borderThickness = 0.001.toRelative()

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
                                    textColor = { defaultTheme.elementColor.value }.toDependent()
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
                                        borderThickness = 0.0025.toRelative()

                                        backgroundColor = { defaultTheme.midgroundColor.value }.toDependent()
                                        borderColor = { defaultTheme.borderColor.value }.toDependent()

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
                                                textColor = { defaultTheme.elementColor.value }.toDependent()
                                            }

                                        children += Text()
                                            .apply {
                                                x = Side.NEGATIVE.toSide()
                                                y = Side.POSITIVE.toSide()
                                                setPadding(Relative(0.175, true))

                                                fontRenderer = "sorus/ui/font/Quicksand-Regular.ttf".toAbsolute()
                                                scale = 0.0035.toRelative()
                                                text = (plugin.third["description"] as String?).toAbsolute()
                                                textColor = { defaultTheme.elementColor.value }.toDependent()
                                            }

                                        children += Container()
                                            .apply {
                                                x = Side.POSITIVE.toSide()
                                                width = 1.0.toCopy()

                                                setPadding(Relative(0.2, true))

                                                backgroundCornerRadius = 0.015.toRelative()
                                                borderThickness = 0.0025.toRelative()

                                                backgroundColor = { defaultTheme.midgroundColor.value }.toDependent()
                                                borderColor = { state: Map<String, Any> ->
                                                    if (state["hovered"] as Boolean) {
                                                        { defaultTheme.selectedBorderColor.value }.toDependent()
                                                    } else {
                                                        Color.fromRGB(10, 10, 10, 150)
                                                    }
                                                }.toDependent()

                                                children += Container()
                                                    .apply {
                                                        width = 0.5.toRelative()
                                                        height = 1.0.toCopy()

                                                        backgroundImage = "sorus/ui/plugins/download.png".toAbsolute()
                                                        backgroundColor = { defaultTheme.elementColor.value }.toDependent()
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
    }

}