/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.enhancements.v1_18_2

import com.github.sorusclient.client.feature.impl.enhancements.Enhancements
import v1_18_2.net.minecraft.client.util.math.MatrixStack

@Suppress("UNUSED")
object EnhancementsHook {

    @JvmStatic
    fun onWrite() {
        Enhancements.saveSettings()
    }

    lateinit var options: Any

    @JvmStatic
    fun onLoad(options: Any) {
        this.options = options
        Enhancements.loadSettings()
    }

    @JvmStatic
    fun onPreRenderFire(matrixStack: MatrixStack) {
        matrixStack.push()
        matrixStack.translate(0.0, -Enhancements.getFireHeightValue() * 0.4, 0.0)
    }

    @JvmStatic
    fun onPostRenderFire(matrixStack: MatrixStack) {
        matrixStack.pop()
    }

    @JvmStatic
    fun modifyBobView(bobView: Boolean): Boolean {
        if (Enhancements.isPartialViewBobbing()) {
            return false
        }

        return bobView
    }

}