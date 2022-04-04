package com.github.sorusclient.client.feature.impl.particles.v1_8_9

import com.github.sorusclient.client.feature.impl.particles.Particles
import kotlin.math.roundToInt

@Suppress("UNUSED")
object ParticlesHook {

    @JvmStatic
    @Suppress("UNUSED")
    fun modifyParticleSpawns(number: Int): Int {
        return (number * Particles.getMultiplierValue()).roundToInt()
    }

    @JvmStatic
    @Suppress("UNUSED")
    fun modifyCriticalParticles(show: Boolean): Boolean {
        if (Particles.getAlwaysCriticalParticles()) {
            return true
        }

        return show
    }

    @JvmStatic
    @Suppress("UNUSED")
    fun modifyEnchantmentParticles(show: Float): Float {
        if (Particles.getAlwaysEnchantmentParticles()) {
            return 1F
        }

        return show
    }

}