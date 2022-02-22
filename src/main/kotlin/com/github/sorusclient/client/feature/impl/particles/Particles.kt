package com.github.sorusclient.client.feature.impl.particles

import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData

class Particles {

    private val multiplier: Setting<Double>
    private val alwaysCriticalParticles: Setting<Boolean>
    private val alwaysEnchantmentParticles: Setting<Boolean>

    init {
        SettingManager.settingsCategory
            .apply {
                data["particles"] = CategoryData()
                    .apply {
                        data["multiplier"] = SettingData(Setting(1.0).also { multiplier = it })
                        data["alwaysCriticalParticles"] = SettingData(Setting(false).also { alwaysCriticalParticles = it })
                        data["alwaysEnchantmentParticles"] = SettingData(Setting(false).also { alwaysEnchantmentParticles = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Particles"))
                    .apply {
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

}