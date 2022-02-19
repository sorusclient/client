package com.github.sorusclient.client.feature

import com.github.sorusclient.client.feature.impl.autogg.AutoGG
import com.github.sorusclient.client.feature.impl.blockoverlay.BlockOverlay
import com.github.sorusclient.client.feature.impl.enhancements.Enhancements
import com.github.sorusclient.client.feature.impl.environmentchanger.EnvironmentChanger
import com.github.sorusclient.client.feature.impl.fullbright.FullBright
import com.github.sorusclient.client.feature.impl.itemphysics.ItemPhysics
import com.github.sorusclient.client.feature.impl.oldanimations.OldAnimations
import com.github.sorusclient.client.feature.impl.particles.Particles
import com.github.sorusclient.client.feature.impl.perspective.Perspective
import com.github.sorusclient.client.feature.impl.togglesprintsneak.ToggleSprintSneak
import com.github.sorusclient.client.feature.impl.zoom.Zoom

object FeatureManager {

    val features: MutableList<Any> = ArrayList()

    init {
        features.add(AutoGG())
        features.add(BlockOverlay())
        features.add(Enhancements())
        features.add(EnvironmentChanger())
        features.add(FullBright())
        features.add(ItemPhysics())
        features.add(OldAnimations())
        features.add(Particles())
        features.add(Perspective())
        features.add(ToggleSprintSneak())
        features.add(Zoom())
    }

    inline fun <reified T> get(): T {
        for (feature in features) {
            if (feature is T) {
                return feature
            }
        }

        return null!!
    }

}