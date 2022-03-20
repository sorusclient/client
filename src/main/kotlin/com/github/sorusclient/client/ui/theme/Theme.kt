package com.github.sorusclient.client.ui.theme

import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.display.DisplayedCategory

abstract class Theme {

    val category: CategoryData = CategoryData()
    val uiCategory: DisplayedCategory = DisplayedCategory("Theme")

    var initialized: Boolean = false

    abstract fun initialize()

    abstract fun onOpenMainGui()
    abstract fun onCloseGui()
    abstract fun onOpenSearchBar()

    abstract fun openSettingsScreen(category: DisplayedCategory)
    abstract fun openUIScreen(tab: String)

    abstract fun onForceClose()
    abstract fun closeGui()

}