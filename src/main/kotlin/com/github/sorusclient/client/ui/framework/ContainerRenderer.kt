/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.ui.framework

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.KeyCharEvent
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.adapter.event.MouseEvent
import com.github.sorusclient.client.adapter.event.RenderEvent
import com.github.sorusclient.client.event.EventManager

object ContainerRenderer {

    private val keyEvents: MutableList<KeyEvent> = ArrayList()
    private val keyCharEvents: MutableList<KeyCharEvent> = ArrayList()
    private val mouseEvents: MutableList<MouseEvent> = ArrayList()

    var containers: MutableList<Container> = ArrayList()

    fun initialize() {
        EventManager.register(KeyEvent::class.java) { e: KeyEvent -> keyEvents.add(e) }
        EventManager.register(MouseEvent::class.java) { e: MouseEvent -> mouseEvents.add(e) }
        EventManager.register(KeyCharEvent::class.java) { e: KeyCharEvent -> keyCharEvents.add(e) }
        EventManager.register(RenderEvent::class.java) { _: RenderEvent -> render() }
    }

    fun open(container: Container) {
        this.containers.add(container)
        container.runtime.setState("hasInit", false)
    }

    fun close(container: Container) {
        this.containers.remove(container)
    }

    fun render() {
        if (containers.isNotEmpty()) {
            val screenDimensions = AdapterManager.getAdapter().screenDimensions
            for (container in containers) {
                container.runtime.render(
                    screenDimensions[0] / 2,
                    screenDimensions[1] / 2,
                    screenDimensions[0],
                    screenDimensions[1]
                )
            }

            for (event in ArrayList(keyEvents)) {
                for (container in ArrayList(containers)) {
                    if (container.runtime.handleKeyEvent(event)) {
                        break
                    }
                }
            }
            for (event in ArrayList(mouseEvents)) {
                for (container in containers.reversed()) {
                    if (container.runtime.handleMouseEvent(event)) {
                        break
                    }
                }
            }
            for (event in ArrayList(keyCharEvents)) {
                for (container in ArrayList(containers)) {
                    if (container.runtime.handleKeyCharEvent(event)) {
                        break
                    }
                }
            }
        }

        keyEvents.clear()
        mouseEvents.clear()
        keyCharEvents.clear()
    }

}