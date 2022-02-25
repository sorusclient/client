package com.github.sorusclient.client.ui.framework

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.adapter.event.MouseEvent
import com.github.sorusclient.client.adapter.event.RenderEvent
import com.github.sorusclient.client.event.EventManager

object ContainerRenderer {

    private val keyEvents: MutableList<KeyEvent> = ArrayList()
    private val mouseEvents: MutableList<MouseEvent> = ArrayList()

    var container: Container? = null

    fun initialize() {
        val eventManager = EventManager
        eventManager.register { e: KeyEvent -> keyEvents.add(e) }
        eventManager.register { e: MouseEvent -> mouseEvents.add(e) }
        eventManager.register { e: RenderEvent -> render() }
    }

    fun open(container: Container) {
        this.container = container
        container.runtime.setState("hasInit", false)
    }

    fun close() {
        this.container = null
    }

    fun render() {
        if (container != null) {
            val screenDimensions = AdapterManager.getAdapter().screenDimensions
            container!!.runtime.render(
                screenDimensions[0] / 2,
                screenDimensions[1] / 2,
                screenDimensions[0],
                screenDimensions[1]
            )

            for (event in keyEvents) {
                container!!.runtime.handleKeyEvent(event)
            }
            for (event in mouseEvents) {
                container!!.runtime.handleMouseEvent(event)
            }
        }

        keyEvents.clear()
        mouseEvents.clear()
    }

}