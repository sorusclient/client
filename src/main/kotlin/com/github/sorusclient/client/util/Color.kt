package com.github.sorusclient.client.util

class Color constructor(
    var red: Double = 0.0,
    var green: Double = 0.0,
    var blue: Double = 0.0,
    var alpha: Double = 0.0
) {

    constructor() : this(0.0, 0.0, 0.0, 0.0)

    val rgb: Int
        get() {
            val red = (red * 255).toInt()
            val green = (green * 255).toInt()
            val blue = (blue * 255).toInt()
            val alpha = (alpha * 255).toInt()
            return alpha and 255 shl 24 or (red and 255 shl 16) or (green and 255 shl 8) or (blue and 255)
        }

    companion object {
        val BLACK: Color
            get() = fromRGB(0, 0, 0, 255)

        val WHITE: Color
            get() = fromRGB(255, 255, 255, 255)

        fun getBetween(percent: Double, color1: Color, color2: Color): Color {
            return Color(
                (color2.red - color1.red) * percent + color1.red,
                (color2.green - color1.green) * percent + color1.green,
                (color2.blue - color1.blue) * percent + color1.blue,
                (color2.alpha - color1.alpha) * percent + color1.alpha
            )
        }

        fun fromRGB(red: Int, green: Int, blue: Int, alpha: Int): Color {
            return Color(
                red / 255.0,
                green / 255.0,
                blue / 255.0,
                alpha / 255.0
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is Color) {
            return red == other.red && green == other.green && blue == other.blue && alpha == other.alpha
        }
        return super.equals(other)
    }

    override fun toString(): String {
        return "Color {r=$red, g=$green, b=$blue, a=$alpha }"
    }

}