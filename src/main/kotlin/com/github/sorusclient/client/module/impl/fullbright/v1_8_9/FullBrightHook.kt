package com.github.sorusclient.client.module.impl.fullbright.v1_8_9

import com.github.sorusclient.client.module.ModuleManager
import com.github.sorusclient.client.module.impl.fullbright.FullBright

object FullBrightHook {

    @JvmStatic
    fun modifyGamma(gamma: Float): Float {
        val fullBright = ModuleManager.get<FullBright>()
        return if (fullBright.isEnabled()) {
            50f
        } else {
            gamma
        }
    }

}