package com.github.sorusclient.client.adapter

import com.github.sorusclient.client.util.Color
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*

interface IRenderer {
    fun draw(buffer: RenderBuffer)
    fun setColor(color: Color)
    fun setLineThickness(thickness: Double)
    fun drawRectangle(x: Double, y: Double, width: Double, height: Double, color: Color) {
        this.drawRectangle(x, y, width, height, 0.0, color)
    }
    fun drawRectangle(x: Double, y: Double, width: Double, height: Double, cornerRadius: Double, color: Color) {
        this.drawRectangle(x, y, width, height, cornerRadius, color, color, color, color)
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

    fun drawRectangleBorder(x: Double, y: Double, width: Double, height: Double, cornerRadius: Double, thickness: Double, color: Color)

    fun drawImage(id: String, x: Double, y: Double, width: Double, height: Double, color: Color) {
        drawImage(id, x, y, width, height, true, color)
    }

    fun drawImage(id: String, x: Double, y: Double, width: Double, height: Double, antialias: Boolean, color: Color) {
        drawImage(id, x, y, width, height, 0.0, antialias, color)
    }

    fun drawImage(id: String, x: Double, y: Double, width: Double, height: Double, cornerRadius: Double, antialias: Boolean, color: Color)

    fun createTexture(path: String) {
        IRenderer::class.java.classLoader.getResource(path)?.let { createTexture(path, it) }
    }

    fun createTexture(id: String, url: URL) {
        try {
            createTexture(id, url.openStream(), true)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun createTexture(id: String, inputStream: InputStream, antialias: Boolean)

    fun scissor(x: Double, y: Double, width: Double, height: Double)
    fun endScissor()
    fun getFontRenderer(id: String): IFontRenderer?

    fun drawTextWithFormatting(id: String, text: String, x: Double, y: Double, scale: Double, color: Color) {
        var tempText = ""
        var index = 0

        var color: Color? = Color.WHITE

        while (index < text.length) {
            if (text[index] == '§') {
                drawText(id, tempText, x, y, scale, color!!)
                color = when (text[index + 1]) {
                    '4' -> Color.fromRGB(170, 0, 0, 255)
                    '6' -> Color.fromRGB(255, 170, 0, 255)
                    'b' -> Color.fromRGB(85, 255, 255, 255)
                    'c' -> Color.fromRGB(255, 85, 85, 255)
                    else -> color
                }
                index += 2
            } else {
                tempText += text[index]
                index += 1
            }
        }
    }

    fun drawText(id: String, text: String, x: Double, y: Double, scale: Double, color: Color)
    fun getTextWidth(fontId: String, text: String): Double
    fun getTextHeight(fontId: String): Double

    fun createFont(path: String) {
        createFont(path, Objects.requireNonNull(IRenderer::class.java.classLoader.getResource(path)))
    }

    fun createFont(id: String, url: URL) {
        try {
            createFont(id, url.openStream())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun createFont(id: String, inputStream: InputStream)

    fun loadBlur()
    fun unloadBlur()

}