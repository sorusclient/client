package com.github.sorusclient.client.feature.v1_8_9

import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.feature.FeatureManager
import com.github.sorusclient.client.feature.impl.enhancements.v1_8_9.Enhancements
import com.github.sorusclient.client.feature.impl.oldanimations.v1_8_9.OldAnimations

class FeatureListener: Listener {

    override fun run() {
        FeatureManager.features.add(Enhancements())
        FeatureManager.features.add(OldAnimations())
    }

}