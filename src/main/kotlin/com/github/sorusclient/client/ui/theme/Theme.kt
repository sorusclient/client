package com.github.sorusclient.client.ui.theme

import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.display.DisplayedCategory

abstract class Theme {

    val category: CategoryData = CategoryData()
    val uiCategory: DisplayedCategory = DisplayedCategory("Theme")

    abstract fun onOpenMainGui()
    abstract fun onCloseGui()
    abstract fun onOpenSearchBar()

    abstract fun openSettingsScreen(category: DisplayedCategory)
    abstract fun openTabScreen(tab: String)

}