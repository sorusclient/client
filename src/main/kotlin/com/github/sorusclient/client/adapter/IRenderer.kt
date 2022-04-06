/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter

import com.github.sorusclient.client.util.Color
import org.apache.commons.io.IOUtils
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

    fun drawRectangleBorder(x: Double, y: Double, width: Double, height: Double, thickness: Double, color: Color) {
        drawRectangleBorder(x, y, width, height, 0.0, thickness, color)
    }

    fun drawRectangleBorder(x: Double, y: Double, width: Double, height: Double, cornerRadius: Double, thickness: Double, color: Color)

    fun drawImage(id: String, x: Double, y: Double, width: Double, height: Double, color: Color) {
        drawImage(id, x, y, width, height, true, color)
    }

    fun drawImage(id: String, x: Double, y: Double, width: Double, height: Double, antialias: Boolean, color: Color) {
        drawImage(id, x, y, width, height, 0.0, antialias, color)
    }

    fun drawImage(id: String, x: Double, y: Double, width: Double, height: Double, cornerRadius: Double, antialias: Boolean, color: Color) {
        drawImage(id, x, y, width, height, cornerRadius, 0.0, 0.0, 1.0, 1.0, antialias, color)
    }

    fun drawImage(id: String, x: Double, y: Double, width: Double, height: Double, cornerRadius: Double, textureX: Double, textureY: Double, textureWidth: Double, textureHeight: Double, antialias: Boolean, color: Color)

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

    fun createTexture(id: String, inputStream: InputStream, antialias: Boolean) {
        createTexture(id, IOUtils.toByteArray(inputStream), antialias)
    }

    fun createTexture(id: String, bytes: ByteArray, antialias: Boolean)

    fun scissor(x: Double, y: Double, width: Double, height: Double)
    fun endScissor()
    fun getFontRenderer(id: String): IFontRenderer?

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