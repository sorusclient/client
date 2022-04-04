package com.github.sorusclient.client.feature.v1_8_9

import com.github.sorusclient.client.bootstrap.Initializer
import com.github.sorusclient.client.feature.impl.enhancements.v1_8_9.Enhancements
import com.github.sorusclient.client.feature.impl.oldanimations.v1_8_9.OldAnimations

@Suppress("UNUSED")
class FeatureListener: Initializer {

    override fun initialize() {
        Enhancements
        OldAnimations
    }

}