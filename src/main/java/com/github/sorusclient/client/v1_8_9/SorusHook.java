package com.github.sorusclient.client.v1_8_9;

import com.github.sorusclient.client.Sorus;

public class SorusHook {

    public static String getBrand() {
        return Sorus.getInstance().getClientBrand();
    }

}
