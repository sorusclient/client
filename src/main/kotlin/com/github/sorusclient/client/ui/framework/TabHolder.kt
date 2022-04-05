/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.ui.framework

import kotlin.collections.List

class TabHolder : Container() {

    private val tabs: MutableMap<String?, Component?> = HashMap()
    var defaultTab: String? = null
    var stateId: String? = null

    //var resetTab = true

    init {
        runtime = Runtime()

        /*onInit += { state ->
            if (resetTab && stateId != null && defaultTab != null) {
                state.second[stateId!!] = defaultTab!!
            }
        }*/
    }

    fun setStateId(stateId: String): TabHolder {
        this.stateId = stateId
        return this
    }

    fun setDefaultTab(defaultTab: String): TabHolder {
        this.defaultTab = defaultTab
        return this
    }

    fun addChild(tab: String, child: Component): TabHolder {
        tabs[tab] = child
        super.addChild(child)
        return this
    }

    override fun addChild(child: Component): Container {
        return this
    }

    inner class Runtime : Container.Runtime() {
        private var prevCurrentTab: String? = null

        override fun getChildren(): List<Component> {
            var currentTab: String? = getState(stateId) as String?
            if (currentTab == null) {
                currentTab = defaultTab
            }

            if (prevCurrentTab != currentTab) {
                //made a question mark instead of exclamation point, not sure if this will kill me in the future
                if (prevCurrentTab != null) {
                    tabs[prevCurrentTab]?.runtime?.setHasInit(false)
                    tabs[prevCurrentTab]?.runtime?.onClose()
                }

                prevCurrentTab = currentTab

                tabs[currentTab]?.runtime?.onInit()
            }

            return if (tabs[currentTab] == null) emptyList() else listOf(tabs[currentTab]!!)
        }

    }
}