/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

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

@Suppress("UNUSED")
class FeatureListener: Initializer {

    override fun initialize() {
        AutoGG
        BlockOverlay
        Enhancements
        EnvironmentChanger
        FullBright
        ItemPhysics
        OldAnimations
        Particles
        Perspective
        ToggleSprintSneak
        Zoom
    }

}