package com.github.sorusclient.client.ui.theme

import com.github.sorusclient.client.adapter.event.InitializeEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.ui.theme.default.DefaultTheme

object ThemeManager {

    lateinit var currentTheme: Theme

    fun initialize() {
        EventManager.register<InitializeEvent> {
            currentTheme = DefaultTheme()
        }
    }

}