package com.github.sorusclient.client.module.impl.oldanimations

import com.github.sorusclient.client.module.ModuleDisableable
import com.github.sorusclient.client.setting.ConfigurableData
import com.github.sorusclient.client.setting.ConfigurableData.Toggle
import com.github.sorusclient.client.setting.Setting

class OldAnimations : ModuleDisableable("oldAnimations") {

    private var oldBlockHit: Setting<Boolean>
    private var showArmorDamage: Setting<Boolean>

    init {
        register("oldBlockHit", Setting(false).also { oldBlockHit = it })
        register("showArmorDamage", Setting(false).also { showArmorDamage = it })
    }

    fun isOldBlockHitValue(): Boolean {
        return oldBlockHit!!.value
    }

    fun showArmorDamageValue(): Boolean {
        return showArmorDamage!!.value
    }

    override fun addSettings(settings: MutableList<ConfigurableData>) {
        super.addSettings(settings)
        settings.add(Toggle("Old Blockhit", oldBlockHit))
        settings.add(Toggle("Show Armor Damage", showArmorDamage))
    }

}