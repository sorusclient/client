package com.github.sorusclient.client.feature.impl.enhancements.v1_18_1

import com.github.sorusclient.client.feature.FeatureManager
import com.github.sorusclient.client.feature.impl.enhancements.Enhancements
import v1_18_1.net.minecraft.client.util.math.MatrixStack

object EnhancementsHook {

    @JvmStatic
    fun onWrite() {
        FeatureManager.get<Enhancements>().saveSettings()
    }

    lateinit var options: Any

    @JvmStatic
    fun onLoad(options: Any) {
        this.options = options
        FeatureManager.get<Enhancements>().loadSettings()
    }

    @JvmStatic
    fun onPreRenderFire(matrixStack: MatrixStack) {
        val enhancements = FeatureManager.get<Enhancements>()
        matrixStack.push()
        matrixStack.translate(0.0, -enhancements.getFireHeightValue() * 0.4, 0.0)
    }

    @JvmStatic
    fun onPostRenderFire(matrixStack: MatrixStack) {
        matrixStack.pop()
    }

}