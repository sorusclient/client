/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.ui.theme.default

import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.ui.*
import com.github.sorusclient.client.ui.framework.Container
import com.github.sorusclient.client.ui.framework.ContainerRenderer
import com.github.sorusclient.client.ui.framework.Scroll
import com.github.sorusclient.client.ui.framework.Text
import com.github.sorusclient.client.ui.framework.constraint.Side
import com.github.sorusclient.client.util.Color

class SearchUI(val defaultTheme: DefaultTheme): Container() {

    init {
        children += Container()
            .apply {
                var searchResults: List<UserInterface.SearchResult> = ArrayList()

                width = 0.25.toRelative()
                height = {
                    (0.12 + if (searchResults.isNotEmpty()) { 0.032 } else { 0.0 } + (searchResults.size * 0.138)).toCopy()
                }.toDependent()

                backgroundCornerRadius = 0.01.toRelative()

                borderThickness = 0.001.toRelative()
                backgroundColor = { defaultTheme.backgroundColor.value }.toDependent()
                borderColor = { defaultTheme.borderColor.value }.toDependent()

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
                                        backgroundColor = { defaultTheme.elementColor.value }.toDependent()
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
                                        textColor = { defaultTheme.elementColor.value }.toDependent()
                                    }

                                var prevKeyTime = System.currentTimeMillis()

                                children += Container()
                                    .apply {
                                        x = Side.NEGATIVE.toSide()
                                        width = 0.05.toRelative()
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
                                            ContainerRenderer.close(this@SearchUI)
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

                                backgroundColor = { defaultTheme.borderColor.value }.toDependent()
                            }

                        onStateUpdate["searchParameter"] = {
                            clearAfter(2)

                            for ((i, result) in searchResults.withIndex()) {
                                children += Container()
                                    .apply {
                                        height = 0.12.toCopy()

                                        setPadding(0.025.toRelative())

                                        backgroundCornerRadius = 0.025.toRelative()

                                        backgroundColor = { defaultTheme.midgroundColor.value }.toDependent()
                                        borderThickness = 0.001.toRelative()
                                        borderColor = { state: Map<String, Any> ->
                                            if (state.getOrDefault("selectedResult", 0) == i) {
                                                { defaultTheme.selectedBorderColor.value }.toDependent()
                                            } else {
                                                { defaultTheme.borderColor.value }.toDependent()
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
                                                textColor = { defaultTheme.elementColor.value }.toDependent()
                                            }
                                    }
                            }
                        }
                    }
            }
    }

}