package com.github.sorusclient.client.feature.impl.enhancements.v1_8_9

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.bootstrap.Initializer

class EnhancementsInitializeListener: Initializer {

    override fun initialize() {
        InterfaceManager.register(SettingsLoader())
    }

}