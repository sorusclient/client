package com.github.sorusclient.client.ui.framework

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.adapter.event.MouseEvent
import com.github.sorusclient.client.adapter.event.RenderEvent
import com.github.sorusclient.client.event.EventManager
import v1_8_9.net.minecraft.client.MinecraftClient

object ContainerRenderer {

    private val keyEvents: MutableList<KeyEvent> = ArrayList()
    private val mouseEvents: MutableList<MouseEvent> = ArrayList()

    var containers: MutableList<Container> = ArrayList()

    fun initialize() {
        val eventManager = EventManager
        eventManager.register { e: KeyEvent -> keyEvents.add(e) }
        eventManager.register { e: MouseEvent -> mouseEvents.add(e) }
        eventManager.register { e: RenderEvent -> render() }
    }

    fun open(container: Container) {
        this.containers.add(container)
        container.runtime.setState("hasInit", false)
    }

    fun close(container: Container) {
        this.containers.remove(container)
    }

    fun close() {
        this.containers.clear()
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

            for (event in keyEvents) {
                for (container in ArrayList(containers)) {
                    if (container.runtime.handleKeyEvent(event)) {
                        break
                    }
                }
            }
            for (event in mouseEvents) {
                for (container in ArrayList(containers)) {
                    if (container.runtime.handleMouseEvent(event)) {
                        break
                    }
                }
            }
        }

        keyEvents.clear()
        mouseEvents.clear()
    }

}