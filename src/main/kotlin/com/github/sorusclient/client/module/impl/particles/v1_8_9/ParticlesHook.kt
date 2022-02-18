package com.github.sorusclient.client.module.impl.particles.v1_8_9

import com.github.sorusclient.client.module.ModuleManager
import com.github.sorusclient.client.module.impl.particles.Particles
import kotlin.math.roundToInt

object ParticlesHook {

    @JvmStatic
    @Suppress("UNUSED")
    fun modifyParticleSpawns(number: Int): Int {
        val particles = ModuleManager.get<Particles>()

        if (particles.isEnabled()) {
            return (number * particles.getMultiplierValue()).roundToInt()
        }

        return number
    }

    @JvmStatic
    @Suppress("UNUSED")
    fun modifyCriticalParticles(show: Boolean): Boolean {
        val particles = ModuleManager.get<Particles>()

        if (particles.isEnabled() && particles.getAlwaysCriticalParticles()) {
            return true
        }

        return show
    }

    @JvmStatic
    @Suppress("UNUSED")
    fun modifyEnchantmentParticles(show: Float): Float {
        val particles = ModuleManager.get<Particles>()

        if (particles.isEnabled() && particles.getAlwaysEnchantmentParticles()) {
            return 1F
        }

        return show
    }

}