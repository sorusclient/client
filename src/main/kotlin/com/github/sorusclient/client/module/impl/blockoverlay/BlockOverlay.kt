package com.github.sorusclient.client.module.impl.blockoverlay

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Point
import com.github.sorusclient.client.adapter.RenderBuffer
import com.github.sorusclient.client.adapter.Vertex
import com.github.sorusclient.client.adapter.event.BlockOutlineRenderEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.module.ModuleDisableable
import com.github.sorusclient.client.setting.ConfigurableData
import com.github.sorusclient.client.setting.ConfigurableData.ColorPicker
import com.github.sorusclient.client.setting.ConfigurableData.Slider
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.util.Color

class BlockOverlay : ModuleDisableable("blockOverlay") {
    private var borderColor: Setting<Color>
    private var borderThickness: Setting<Double>
    private var fillColor: Setting<Color>

    init {
        register("borderColor", Setting(Color.BLACK).also { borderColor = it })
        register("borderThickness", Setting(1.0).also { borderThickness = it })
        register("fillColor", Setting(Color.fromRGB(0, 0, 0, 0)).also { fillColor = it })
        EventManager.register(this::preRenderBlockOutline)
    }

    private fun preRenderBlockOutline(event: BlockOutlineRenderEvent) {
        if (!isEnabled()) {
            return
        }
        val box = event.box
        val buffer = RenderBuffer()
        buffer.drawMode = RenderBuffer.DrawMode.QUAD
        val fillColor = getFillColor()
        buffer.push(Vertex().setPoint(Point(box.minX, box.minY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.maxY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.maxY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.minY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.minY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.maxY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.maxY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.minY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.minY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.maxY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.maxY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.minY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.minY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.maxY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.maxY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.minY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.maxY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.maxY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.maxY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.maxY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.minY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.minY, box.maxZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.minX, box.minY, box.minZ)).setColor(fillColor))
        buffer.push(Vertex().setPoint(Point(box.maxX, box.minY, box.minZ)).setColor(fillColor))
        val renderer = AdapterManager.getAdapter().renderer
        renderer.draw(buffer)
        renderer.setLineThickness(getBorderThickness())
        renderer.setColor(getBorderColor())
    }

    private fun getBorderColor(): Color {
        return borderColor.value
    }

    private fun getBorderThickness(): Double {
        return borderThickness.value
    }

    private fun getFillColor(): Color {
        return fillColor.value
    }

    override fun addSettings(settings: MutableList<ConfigurableData>) {
        super.addSettings(settings)
        settings.add(Slider("Border Thickness", borderThickness, 0.0, 5.0))
        settings.add(ColorPicker("Border Color", borderColor))
        settings.add(ColorPicker("Fill Color", fillColor))
    }
}