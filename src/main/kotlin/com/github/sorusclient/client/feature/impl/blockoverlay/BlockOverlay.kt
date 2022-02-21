package com.github.sorusclient.client.feature.impl.blockoverlay

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Point
import com.github.sorusclient.client.adapter.RenderBuffer
import com.github.sorusclient.client.adapter.Vertex
import com.github.sorusclient.client.adapter.event.BlockOutlineRenderEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData
import com.github.sorusclient.client.util.Color

class BlockOverlay {

    private var enabled: Setting<Boolean>
    private var borderColor: Setting<Color>
    private var borderThickness: Setting<Double>
    private var fillColor: Setting<Color>

    init {
        SettingManager.settingsCategory
            .apply {
                data["blockOverlay"] = CategoryData()
                    .apply {
                        data["enabled"] = SettingData(Setting(false).also { enabled = it })
                        data["borderColor"] = SettingData(Setting(Color.BLACK).also { borderColor = it })
                        data["borderThickness"] = SettingData(Setting(1.0).also { borderThickness = it })
                        data["fillColor"] = SettingData(Setting(Color.fromRGB(0, 0, 0, 0)).also { fillColor = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Block Overlay"))
                    .apply {
                        add(Toggle(enabled, "Enabled"))
                        add(ColorPicker(borderColor, "Border Color", ))
                        add(Slider(borderThickness, "Border Thickness", 0.0, 5.0))
                        add(ColorPicker(fillColor, "Fill Color", ))
                    }
            }

        EventManager.register(this::preRenderBlockOutline)
    }

    private fun preRenderBlockOutline(event: BlockOutlineRenderEvent) {
        if (!enabled.value) {
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

}