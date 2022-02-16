package com.github.sorusclient.client.adapter

import com.github.sorusclient.client.util.Color

interface IFontRenderer {
    fun drawString(text: String?, x: Double, y: Double, scale: Double, color: Color)
    fun getWidth(text: String?): Double
    fun getHeight(): Double
    fun drawStringShadowed(text: String?, x: Double, y: Double, scale: Double, color: Color, shadowColor: Color) {
        drawString(text, x + 1 * scale, y + 1 * scale, scale, shadowColor)
        drawString(text, x, y, scale, color)
    }
}