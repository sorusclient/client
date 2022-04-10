/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.ui.theme.default

import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.ui.*
import com.github.sorusclient.client.ui.framework.Container
import com.github.sorusclient.client.ui.framework.List
import com.github.sorusclient.client.ui.framework.TabHolder
import com.github.sorusclient.client.ui.framework.constraint.Dependent
import com.github.sorusclient.client.ui.framework.constraint.Side

class MainGui(private val defaultTheme: DefaultTheme) : Container() {
    
    init {
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

                        backgroundColor = { defaultTheme.backgroundColor.value }.toDependent()
                        borderColor = { defaultTheme.borderColor.value }.toDependent()
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
                                        backgroundColor = { defaultTheme.elementColor.value }.toDependent()
                                    })

                                addChild(Container()
                                    .apply {
                                        width = 0.8.toRelative()
                                        height = 0.6.toAbsolute()
                                        setPadding(0.1.toRelative())

                                        backgroundColor = { defaultTheme.borderColor.value }.toDependent()
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
                                                    { defaultTheme.selectedColor.value }.toDependent()
                                                } else {
                                                    { defaultTheme.midgroundColor.value }.toDependent()
                                                }
                                            }

                                            borderColor = Dependent { state ->
                                                return@Dependent if (state["tab"] == tab || (tab == "home" && state["tab"] == null) || state["hovered"] as Boolean) {
                                                    { defaultTheme.selectedBorderColor.value }.toDependent()
                                                } else {
                                                    { defaultTheme.borderColor.value }.toDependent()
                                                }
                                            }

                                            borderThickness = 0.4.toAbsolute()

                                            children += Container()
                                                .apply {
                                                    width = 0.5.toRelative()
                                                    height = 0.5.toRelative()

                                                    backgroundImage = "sorus/ui/navbar/$tab.png".toAbsolute()
                                                    backgroundColor = { defaultTheme.elementColor.value }.toDependent()
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

                        resetTab = false

                        transitionFadeTime = 275

                        cancelAnimationsState = "cancelMenuAnimation"

                        addChild("home", Container())
                        addChild("settings", SettingsTab(defaultTheme))
                        addChild("plugins", PluginsTab(defaultTheme))
                        addChild("themes", ThemesTab(defaultTheme))
                        addChild("social", SocialTab(defaultTheme))

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
                storedState += "cancelMenuAnimation"
            }
    }

}