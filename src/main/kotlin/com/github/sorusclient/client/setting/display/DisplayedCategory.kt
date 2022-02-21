package com.github.sorusclient.client.setting.display

open class DisplayedCategory(val displayName: String): Displayed() {

    val components: MutableList<Displayed> = ArrayList()
    var parent: DisplayedCategory? = null
    open var showUI = true
    open var `return` = false
    open var wantedOpenCategory: DisplayedCategory? = null

    fun <T: Displayed> add(displayed: T): T {
        this.components.add(displayed)

        if (displayed is DisplayedCategory) {
            displayed.parent = this
        }

        return displayed
    }

    open fun onShow() {

    }

    open fun onHide() {

    }

}