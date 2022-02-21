package com.github.sorusclient.client.feature.impl.particles.v1_8_9

import com.github.sorusclient.client.feature.FeatureManager
import com.github.sorusclient.client.feature.impl.particles.Particles
import kotlin.math.roundToInt

object ParticlesHook {

    @JvmStatic
    @Suppress("UNUSED")
    fun modifyParticleSpawns(number: Int): Int {
        val particles = FeatureManager.get<Particles>()

        if (particles.isEnabled()) {
            return (number * particles.getMultiplierValue()).roundToInt()
        }

        return number
    }

    @JvmStatic
    @Suppress("UNUSED")
    fun modifyCriticalParticles(show: Boolean): Boolean {
        val particles = FeatureManager.get<Particles>()

        if (particles.isEnabled() && particles.getAlwaysCriticalParticles()) {
            return true
        }

        return show
    }

    @JvmStatic
    @Suppress("UNUSED")
    fun modifyEnchantmentParticles(show: Float): Float {
        val particles = FeatureManager.get<Particles>()

        if (particles.isEnabled() && particles.getAlwaysEnchantmentParticles()) {
            return 1F
        }

        return show
    }

}