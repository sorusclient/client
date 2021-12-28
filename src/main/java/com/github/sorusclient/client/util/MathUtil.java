package com.github.sorusclient.client.util;

public class MathUtil {

    public static double clamp(double value, double lower, double upper) {
        return Math.max(Math.min(value, upper), lower);
    }

}
