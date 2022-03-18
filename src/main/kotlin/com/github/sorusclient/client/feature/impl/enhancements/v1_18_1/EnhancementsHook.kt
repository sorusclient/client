package com.github.sorusclient.client.feature.impl.enhancements.v1_18_1

import com.github.sorusclient.client.feature.FeatureManager
import com.github.sorusclient.client.feature.impl.enhancements.Enhancements
import org.apache.commons.io.FileUtils
import v1_18_1.net.minecraft.client.option.GameOptions
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.properties.Delegates

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

}