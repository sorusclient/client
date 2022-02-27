package com.github.sorusclient.client.adapter

import com.github.sorusclient.client.util.Color

interface IFontRenderer {
    fun drawString(text: String, x: Double, y: Double, scale: Double, color: Color, shadow: Boolean)
    fun getWidth(text: String): Double
    fun getWidth(text: IText): Double
    fun getHeight(): Double
    fun drawString(text: String, x: Double, y: Double, scale: Double, color: Color) {
        drawString(text, x, y, scale, color, false)
    }

    fun drawString(text: IText, x: Double, y: Double, scale: Double, color: Color, shadow: Boolean)
    fun drawString(text: IText, x: Double, y: Double, scale: Double, color: Color) {
        drawString(text, x, y, scale, color, false)
    }
}