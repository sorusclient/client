/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.setting.display

import com.github.sorusclient.client.ui.framework.Container

open class DisplayedCategory(val displayName: String): Displayed() {

    val components: MutableList<Displayed> = ArrayList()
    open var showUI = true
    var `return` = false
    var wantedOpenCategory: DisplayedCategory? = null
    var customUI: Container? = null

    fun <T: Displayed> add(displayed: T): T {
        var displayed2 = displayed

        if (displayed2 is DisplayedCategory) {
            for (component in components) {
                if (component is DisplayedCategory && component.displayName == (displayed2 as DisplayedCategory).displayName) {
                    displayed2 = component as T
                }
            }
        }

        if (displayed == displayed2) {
            this.components.add(displayed)
            displayed.parent = this
        }

        return displayed2
    }

    open fun onShow() {

    }

    open fun onHide() {

    }

    override val name: String
        get() = displayName

}