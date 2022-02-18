package com.github.sorusclient.client.module.impl.particles

import com.github.sorusclient.client.module.ModuleDisableable
import com.github.sorusclient.client.setting.ConfigurableData
import com.github.sorusclient.client.setting.Setting

class Particles: ModuleDisableable("particles") {

    private val multiplier: Setting<Double>
    private val alwaysCriticalParticles: Setting<Boolean>
    private val alwaysEnchantmentParticles: Setting<Boolean>

    init {
        register("multiplier", Setting(1.0).also { multiplier = it })
        register("alwaysCriticalParticles", Setting(false).also { alwaysCriticalParticles = it })
        register("alwaysEnchantmentParticles", Setting(false).also { alwaysEnchantmentParticles = it })
    }

    override fun addSettings(settings: MutableList<ConfigurableData>) {
        super.addSettings(settings)
        settings.add(ConfigurableData.Slider("Multiplier", multiplier, 0.5, 5.0))
        settings.add(ConfigurableData.Toggle("Always Critical Particles", alwaysCriticalParticles))
        settings.add(ConfigurableData.Toggle("Always Enchantment Particles", alwaysEnchantmentParticles))
    }

    fun getMultiplierValue(): Double {
        return multiplier.value
    }

    fun getAlwaysCriticalParticles(): Boolean {
        return alwaysCriticalParticles.value
    }

    fun getAlwaysEnchantmentParticles(): Boolean {
        return alwaysEnchantmentParticles.value
    }

}