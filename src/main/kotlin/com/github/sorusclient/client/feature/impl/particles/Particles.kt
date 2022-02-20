package com.github.sorusclient.client.feature.impl.particles

import com.github.sorusclient.client.setting.Category
import com.github.sorusclient.client.setting.SettingConfigure.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager

class Particles {

    private val enabled: Setting<Boolean>
    private val multiplier: Setting<Double>
    private val alwaysCriticalParticles: Setting<Boolean>
    private val alwaysEnchantmentParticles: Setting<Boolean>

    init {
        SettingManager.settingsCategory
            .apply {
                put("particles", HashMap<String, Any>()
                    .apply {
                        put("enabled", Setting(false).also { enabled = it })
                        put("multiplier", Setting(1.0).also { multiplier = it })
                        put("alwaysCriticalParticles", Setting(false).also { alwaysCriticalParticles = it })
                        put("alwaysEnchantmentParticles", Setting(false).also { alwaysEnchantmentParticles = it })
                    })
            }

        SettingManager.mainUICategory
            .apply {
                add(Category("Particles"))
                    .apply {
                        add(Toggle(enabled, "Enabled"))
                        add(Slider(multiplier, "Multiplier", 0.5, 5.0))
                        add(Toggle(alwaysCriticalParticles, "Allows Show Critical Particles", ))
                        add(Toggle(alwaysEnchantmentParticles, "Allows Show Enchantment Particles", ))
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