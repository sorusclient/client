package com.github.sorusclient.client.feature

import com.github.sorusclient.client.bootstrap.Initializer
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

class FeatureListener: Initializer {

    override fun initialize() {
        FeatureManager.features.add(AutoGG())
        FeatureManager.features.add(BlockOverlay())
        FeatureManager.features.add(Enhancements())
        FeatureManager.features.add(EnvironmentChanger())
        FeatureManager.features.add(FullBright())
        FeatureManager.features.add(ItemPhysics())
        FeatureManager.features.add(OldAnimations())
        FeatureManager.features.add(Particles())
        FeatureManager.features.add(Perspective())
        FeatureManager.features.add(ToggleSprintSneak())
        FeatureManager.features.add(Zoom())
    }

}