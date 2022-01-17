package com.github.sorusclient.client.v1_8_9

import com.github.sorusclient.client.Sorus

object SorusHook {

    @JvmStatic
    val brand: String
        get() = Sorus.instance.clientBrand

}