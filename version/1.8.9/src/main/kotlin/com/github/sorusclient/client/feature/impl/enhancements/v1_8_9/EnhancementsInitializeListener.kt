package com.github.sorusclient.client.feature.impl.enhancements.v1_8_9

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.feature.impl.enhancements.ISettingsLoader

class EnhancementsInitializeListener: Listener {

    override fun run() {
        GlassLoader.getInstance().registerInterface(ISettingsLoader::class.java, SettingsLoader())
    }

}