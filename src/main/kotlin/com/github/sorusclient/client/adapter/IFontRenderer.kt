/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter

import com.github.sorusclient.client.util.Color

interface IFontRenderer {
    fun drawString(text: String, x: Double, y: Double, scale: Double, color: Color, shadow: Boolean)
    fun drawString(text: String, x: Double, y: Double, scale: Double, color: Color) {
        drawString(text, x, y, scale, color, false)
    }
    fun getWidth(text: String): Double

    fun drawString(text: IText, x: Double, y: Double, scale: Double, color: Color, shadow: Boolean)
    fun drawString(text: IText, x: Double, y: Double, scale: Double, color: Color) {
        drawString(text, x, y, scale, color, false)
    }
    fun getWidth(text: IText): Double

    fun getHeight(): Double
}