/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.particles.v1_18_2

import com.github.sorusclient.client.feature.impl.particles.Particles
import kotlin.math.roundToInt

@Suppress("UNUSED")
object ParticlesHook {

    @JvmStatic
    fun modifyParticleSpawns(number: Int): Int {
        return (number * Particles.getMultiplierValue()).roundToInt()
    }

    @JvmStatic
    fun modifyCriticalParticles(show: Boolean): Boolean {
        if (Particles.getAlwaysCriticalParticles()) {
            return true
        }

        return show
    }

    @JvmStatic
    fun modifyEnchantmentParticles(show: Float): Float {
        if (Particles.getAlwaysEnchantmentParticles()) {
            return 1F
        }

        return show
    }

}