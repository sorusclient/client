package com.github.sorusclient.client.util;

public class Color {

    public static final Color BLACK = Color.fromRGB(0, 0, 0, 255);
    public static final Color WHITE = Color.fromRGB(255, 255, 255, 255);

    public static Color getBetween(double percent, Color color1, Color color2) {
        return new Color((color2.red - color1.red) * percent + color1.red,
                (color2.green - color1.green) * percent + color1.green,
                (color2.blue - color1.blue) * percent + color1.blue,
                (color2.alpha - color1.alpha) * percent + color1.alpha);
    }

    public static Color fromRGB(int red, int green, int blue, int alpha) {
        return new Color(
            red / 255.0,
                green / 255.0,
                blue / 255.0,
                alpha / 255.0
        );
    }

    private final double red, green, blue, alpha;

    private Color() {
        this(0, 0, 0, 0);
    }

    private Color(double red, double green, double blue, double alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public double getRed() {
        return red;
    }

    public double getGreen() {
        return green;
    }

    public double getBlue() {
        return blue;
    }

    public double getAlpha() {
        return alpha;
    }

    public int getRgb() {
        int red = (int) (this.red * 255);
        int green = (int) (this.green * 255);
        int blue = (int) (this.blue * 255);
        int alpha = (int) (this.alpha * 255);

        return (alpha & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | (blue & 255);
    }

}
