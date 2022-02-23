package com.github.sorusclient.client.ui.theme

import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.ui.theme.default.DefaultTheme

object ThemeManager {

    lateinit var currentTheme: Theme

    fun initialize() {
        currentTheme = DefaultTheme()

        SettingManager.settingsCategory
            .apply {
                data["theme"] = currentTheme.category
            }

        SettingManager.mainUICategory
            .apply {
                add(currentTheme.uiCategory)
            }
    }

}