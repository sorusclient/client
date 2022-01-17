package com.github.sorusclient.client.adapter

import com.github.sorusclient.client.util.Color

interface IRenderer {
    fun draw(buffer: RenderBuffer)
    fun setColor(color: Color)
    fun setLineThickness(thickness: Double)
    fun drawRectangle(x: Double, y: Double, width: Double, height: Double, color: Color) {
        this.drawRectangle(x, y, width, height, 0.0, color, color, color, color)
    }

    fun drawRectangle(
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        cornerRadius: Double,
        topLeftColor: Color,
        bottomLeftColor: Color,
        bottomRightColor: Color,
        topRightColor: Color
    )

    fun drawImage(imagePath: String?, x: Double, y: Double, width: Double, height: Double, color: Color)
    fun scissor(x: Double, y: Double, width: Double, height: Double)
    fun endScissor()
    fun getFontRenderer(id: String): IFontRenderer?
}