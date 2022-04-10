/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.ui.theme.default

import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting
import com.github.sorusclient.client.ui.*
import com.github.sorusclient.client.ui.framework.Container
import com.github.sorusclient.client.ui.framework.List
import com.github.sorusclient.client.ui.framework.Scroll
import com.github.sorusclient.client.ui.framework.Text
import com.github.sorusclient.client.ui.framework.constraint.Dependent
import com.github.sorusclient.client.ui.framework.constraint.Relative
import com.github.sorusclient.client.ui.framework.constraint.Side
import com.github.sorusclient.client.util.Color
import kotlin.math.ceil

class SettingsTab(private val defaultTheme: DefaultTheme) : Container() {

    init {
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

                backgroundColor = { defaultTheme.backgroundColor.value }.toDependent()
                borderColor = { defaultTheme.borderColor.value }.toDependent()
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
                                    textColor = { defaultTheme.elementColor.value }.toDependent()
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
                                    textColor = { defaultTheme.elementColor.value }.toDependent()
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
                                            x = Side.NEGATIVE.toSide()
                                            width = 1.0.toCopy()
                                            height = 0.5.toRelative()
                                            setPadding(Relative(0.2, true))

                                            backgroundImage = "sorus/ui/profiles/create.png".toAbsolute()
                                            backgroundColor = { defaultTheme.elementColor.value }.toDependent()
                                        }

                                    children += Text()
                                        .apply {
                                            x = Side.NEGATIVE.toSide()
                                            setPadding(Relative(0.2, true))

                                            scale = 0.012.toRelative()
                                            fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                            text = "Create".toAbsolute()
                                            textColor = { defaultTheme.elementColor.value }.toDependent()
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
                                            x = Side.NEGATIVE.toSide()
                                            width = 1.0.toCopy()
                                            height = 0.5.toRelative()
                                            setPadding(Relative(0.2, true))

                                            backgroundImage = "sorus/ui/profiles/delete.png".toAbsolute()
                                            backgroundColor = { defaultTheme.elementColor.value }.toDependent()
                                        }

                                    children += Text()
                                        .apply {
                                            x = Side.NEGATIVE.toSide()
                                            y = Side.ZERO.toSide()
                                            setPadding(Relative(0.2, true))

                                            scale = 0.012.toRelative()
                                            fontRenderer = "sorus/ui/font/Quicksand-SemiBold.ttf".toAbsolute()
                                            text = "Delete".toAbsolute()
                                            textColor = { defaultTheme.elementColor.value }.toDependent()
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

                            backgroundColor = { defaultTheme.borderColor.value }.toDependent()
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
                                                            { defaultTheme.selectedColor.value }.toDependent()
                                                        } else {
                                                            { defaultTheme.midgroundColor.value }.toDependent()
                                                        }
                                                    }

                                                    borderColor = Dependent { state ->
                                                        return@Dependent if (SettingManager.currentProfile == profile.first || state["hovered"] as Boolean) {
                                                            { defaultTheme.selectedBorderColor.value }.toDependent()
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
                                                            textColor = { defaultTheme.elementColor.value }.toDependent()
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

                backgroundColor = { defaultTheme.backgroundColor.value }.toDependent()
                borderColor = { defaultTheme.borderColor.value }.toDependent()
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
                                    textColor = { defaultTheme.elementColor.value }.toDependent()
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
                                                                { defaultTheme.selectedColor.value }.toDependent()
                                                            } else {
                                                                { defaultTheme.midgroundColor.value }.toDependent()
                                                            }
                                                        }
                                                        borderThickness = 0.4.toAbsolute()
                                                        borderColor = { defaultTheme.borderColor.value }.toDependent()
                                                        borderColor = Dependent { state ->
                                                            if ((state["clicked"] != null && state["clicked"] as Boolean) || state["hovered"] as Boolean) {
                                                                { defaultTheme.selectedBorderColor.value }.toDependent()
                                                            } else {
                                                                { defaultTheme.borderColor.value }.toDependent()
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
                                                                textColor = { defaultTheme.elementColor.value }.toDependent()
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

                                    backgroundColor = { defaultTheme.borderColor.value }.toDependent()
                                })

                            for (setting in category.components) {
                                if (setting is DisplayedSetting) {
                                    addChild(defaultTheme.getSetting(setting)
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

                                                    children += Text()
                                                        .apply {
                                                            scale = 0.011.toRelative()
                                                            fontRenderer = "sorus/ui/font/Quicksand-Medium.ttf".toAbsolute()
                                                            text = "Reset".toAbsolute()
                                                            textColor = { defaultTheme.elementColor.value }.toDependent()
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
    }

}