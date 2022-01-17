package com.github.sorusclient.client.ui.framework

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.adapter.event.MouseEvent
import com.github.sorusclient.client.event.EventManager

object ContainerRenderer {
    private val keyEvents: MutableList<KeyEvent> = ArrayList()
    private val mouseEvents: MutableList<MouseEvent> = ArrayList()
    fun initialize() {
        val eventManager = EventManager
        eventManager.register(KeyEvent::class.java) { e: KeyEvent -> keyEvents.add(e) }
        eventManager.register(MouseEvent::class.java) { e: MouseEvent -> mouseEvents.add(e) }
    }

    fun render(container: Container?) {
        val screenDimensions = AdapterManager.getAdapter().screenDimensions
        container!!.runtime.render(
            screenDimensions[0] / 2,
            screenDimensions[1] / 2,
            screenDimensions[0],
            screenDimensions[1]
        )
        for (event in keyEvents) {
            container.runtime.handleKeyEvent(event)
        }
        for (event in mouseEvents) {
            container.runtime.handleMouseEvent(event)
        }
        keyEvents.clear()
        mouseEvents.clear()
    }
}