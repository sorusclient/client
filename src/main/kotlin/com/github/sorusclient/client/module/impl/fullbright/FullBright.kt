package com.github.sorusclient.client.module.impl.fullbright

import com.github.sorusclient.client.adapter.event.GetGammaEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.module.ModuleDisableable

class FullBright : ModuleDisableable("fullBright") {

    init {
        EventManager.register<GetGammaEvent> { event ->
            if (this.isEnabled()) {
                event.gamma = 100.0
            }
        }
    }

}