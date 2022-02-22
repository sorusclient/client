package com.github.sorusclient.client.setting.display

import com.github.sorusclient.client.ui.framework.Container

open class DisplayedCategory(val displayName: String): Displayed() {

    val components: MutableList<Displayed> = ArrayList()
    open var showUI = true
    var `return` = false
    var wantedOpenCategory: DisplayedCategory? = null
    var customUI: Container? = null

    fun <T: Displayed> add(displayed: T): T {
        this.components.add(displayed)
        displayed.parent = this

        return displayed
    }

    open fun onShow() {

    }

    open fun onHide() {

    }

    override val name: String
        get() = displayName

}