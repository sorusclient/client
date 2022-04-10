/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.ui.theme.default

import com.github.sorusclient.client.setting.display.DisplayedSetting
import com.github.sorusclient.client.ui.*
import com.github.sorusclient.client.ui.framework.*
import com.github.sorusclient.client.ui.framework.constraint.Dependent
import com.github.sorusclient.client.ui.framework.constraint.Relative
import com.github.sorusclient.client.ui.framework.constraint.Side
import com.github.sorusclient.client.ui.theme.Theme
import com.github.sorusclient.client.ui.theme.ThemeManager
import com.github.sorusclient.client.util.Color
import kotlin.math.max
import kotlin.math.min

class ThemesTab(private val defaultTheme: DefaultTheme): Container() {

    init {
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

                backgroundColor = { defaultTheme.backgroundColor.value }.toDependent()
                borderColor = { defaultTheme.borderColor.value }.toDependent()
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
                                            textColor = { defaultTheme.elementColor.value }.toDependent()
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
                                                    defaultTheme.midgroundColor.value
                                                }.toDependent()

                                                borderColor = {
                                                    defaultTheme.borderColor.value
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
                                                        textColor = { defaultTheme.elementColor.value }.toDependent()
                                                    }

                                                children += Container()
                                                    .apply {
                                                        x = Side.POSITIVE.toSide()
                                                        width = 1.0.toCopy()

                                                        setPadding(Relative(0.2, true))

                                                        backgroundCornerRadius = 0.015.toRelative()
                                                        borderThickness = 0.4.toAbsolute()

                                                        backgroundColor = { defaultTheme.midgroundColor.value }.toDependent()

                                                        borderColor = { state: Map<String, Any> ->
                                                            if (state["hovered"] as Boolean && ThemeManager.configuredThemes.indexOf(theme) < ThemeManager.configuredThemes.size - 1) {
                                                                { defaultTheme.selectedBorderColor.value }.toDependent()
                                                            } else {
                                                                { defaultTheme.borderColor.value }.toDependent()
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

                                                                backgroundImage = "sorus/ui/navbar/settings.png".toAbsolute()
                                                                backgroundColor = { defaultTheme.elementColor.value }.toDependent()
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

                                                                backgroundColor = { defaultTheme.midgroundColor.value }.toDependent()

                                                                borderColor = { defaultTheme.borderColor.value }.toDependent()
                                                                borderColor = { state: Map<String, Any> ->
                                                                    if (state["hovered"] as Boolean && ThemeManager.configuredThemes.indexOf(theme) > 0) {
                                                                        { defaultTheme.selectedBorderColor.value }.toDependent()
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

                                                                backgroundColor = { defaultTheme.midgroundColor.value }.toDependent()

                                                                borderColor = { defaultTheme.borderColor.value }.toDependent()
                                                                borderColor = { state: Map<String, Any> ->
                                                                    if (state["hovered"] as Boolean && ThemeManager.configuredThemes.indexOf(theme) < ThemeManager.configuredThemes.size - 1) {
                                                                        { defaultTheme.selectedBorderColor.value }.toDependent()
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
                                            textColor = { defaultTheme.elementColor.value }.toDependent()
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
                                                    { defaultTheme.selectedColor.value }.toDependent()
                                                } else {
                                                    { defaultTheme.midgroundColor.value }.toDependent()
                                                }
                                            }
                                            borderThickness = 0.4.toAbsolute()
                                            borderColor = Dependent { state ->
                                                if ((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) {
                                                    { defaultTheme.selectedBorderColor.value }.toDependent()
                                                } else {
                                                    { defaultTheme.borderColor.value }.toDependent()
                                                }
                                            }

                                            children += Container()
                                                .apply {
                                                    width = 0.5.toRelative()
                                                    height = 0.5.toRelative()

                                                    backgroundImage = "sorus/ui/settings/back.png".toAbsolute()
                                                    backgroundColor = { defaultTheme.elementColor.value }.toDependent()
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
                                            addChild(defaultTheme.getSetting(setting))
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

                backgroundColor = { defaultTheme.backgroundColor.value }.toDependent()
                borderColor = { defaultTheme.borderColor.value }.toDependent()
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
                                    textColor = { defaultTheme.elementColor.value }.toDependent()
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
                                            { defaultTheme.midgroundColor.value }.toDependent()
                                        borderColor =
                                            { defaultTheme.borderColor.value }.toDependent()

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
                                                textColor = { defaultTheme.elementColor.value }.toDependent()
                                            }

                                        children += Container()
                                            .apply {
                                                x = Side.POSITIVE.toSide()
                                                width = 1.0.toCopy()

                                                setPadding(Relative(0.2, true))

                                                backgroundCornerRadius =
                                                    0.015.toRelative()
                                                borderThickness = 0.4.toAbsolute()

                                                backgroundColor = { defaultTheme.midgroundColor.value }.toDependent()
                                                borderColor =
                                                    { state: Map<String, Any> ->
                                                        if (state["hovered"] as Boolean) {
                                                            { defaultTheme.selectedBorderColor.value }.toDependent()
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
                                                        backgroundColor = { defaultTheme.elementColor.value }.toDependent()
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
    }

}