package com.github.sorusclient.client.feature.impl.enhancements.v1_18_2

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.bootstrap.Initializer

class EnhancementsInitializeListener: Initializer {

    override fun initialize() {
        InterfaceManager.register(SettingsLoader())
    }

}