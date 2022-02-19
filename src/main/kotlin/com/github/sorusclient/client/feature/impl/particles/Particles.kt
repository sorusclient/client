package com.github.sorusclient.client.feature.impl.particles

import com.github.sorusclient.client.setting.DisplayedCategory
import com.github.sorusclient.client.setting.DisplayedSetting.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager

class Particles {

    private val enabled: Setting<Boolean>
    private val multiplier: Setting<Double>
    private val alwaysCriticalParticles: Setting<Boolean>
    private val alwaysEnchantmentParticles: Setting<Boolean>

    init {
        SettingManager.mainCategory
            .apply {
                registerDisplayed(DisplayedCategory("Particles"))
                    .apply {
                        registerDisplayed(Toggle("Enabled", Setting(false).also { enabled = it }))
                        registerDisplayed(Slider("Multiplier", Setting(1.0).also { multiplier = it }, 0.5, 5.0))
                        registerDisplayed(Toggle("Allows Show Critical Particles", Setting(false).also { alwaysCriticalParticles = it }))
                        registerDisplayed(Toggle("Allows Show Enchantment Particles", Setting(false).also { alwaysEnchantmentParticles = it }))
                    }
            }
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

    fun isEnabled(): Boolean {
        return enabled.value
    }

}