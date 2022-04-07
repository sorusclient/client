/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.ui.framework

import com.github.sorusclient.client.ui.toDependent
import com.github.sorusclient.client.util.Color
import kotlin.collections.List
import kotlin.math.min

class TabHolder : Container() {

    private val tabs: MutableMap<String?, Component?> = HashMap()
    var defaultTab: String? = null
    var stateId: String? = null
    var cancelAnimationsState: String? = null

    var resetTab = true

    // In milliseconds
    var transitionFadeTime = 0

    init {
        runtime = Runtime()

        storedState += "switchTime"

        onInit += { state ->
            if (resetTab && stateId != null && defaultTab != null) {
                state.second[stateId!!] = defaultTab!!
            }
        }
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
        private var prevTab: Component? = null

        override fun getChildren(): List<Component> {
            var currentTab: String? = getState(stateId) as String?
            if (currentTab == null) {
                currentTab = defaultTab
            }

            if (prevCurrentTab != currentTab) {
                if (prevCurrentTab != null) {
                    tabs[prevCurrentTab]?.runtime?.setHasInit(false)
                    tabs[prevCurrentTab]?.runtime?.onClose()
                    prevTab = tabs[prevCurrentTab]
                }

                (tabs[prevCurrentTab] as Container?)?.transmitColor = { state: Map<String, Any> ->
                    val switchTime = state["switchTime"] as Long
                    Color(1.0, 1.0, 1.0, min(1.0, 1.0 - (System.currentTimeMillis() - switchTime) / (transitionFadeTime.toDouble() / 2)))
                }.toDependent()
                (tabs[currentTab] as Container?)?.transmitColor = { state: Map<String, Any> ->
                    val switchTime = state["switchTime"] as Long
                    Color(1.0, 1.0, 1.0, min(1.0, ((System.currentTimeMillis() - switchTime) - transitionFadeTime / 2) / (transitionFadeTime.toDouble() / 2)))
                }.toDependent()

                prevCurrentTab = currentTab

                tabs[currentTab]?.runtime?.onInit()

                setState("switchTime", System.currentTimeMillis())

                /*if (cancelAnimationsState == null || getState(cancelAnimationsState) == null || !(getState(cancelAnimationsState) as Boolean)) {

                } else {
                    if (cancelAnimationsState != null) {
                        setState(cancelAnimationsState!!, false)
                    }
                }*/
            }

            return if ((System.currentTimeMillis() - (getState("switchTime") as Long? ?: 0L)) < transitionFadeTime / 2) {
                val list = mutableListOf<Component>()
                tabs[currentTab]?.let { list.add(it) }
                prevTab?.let { list.add(it) }
                list
            } else {
                val list = mutableListOf<Component>()
                tabs[currentTab]?.let { list.add(it) }
                list
            }
        }

    }
}