package com.github.sorusclient.client.module

import com.github.sorusclient.client.setting.DisplayedSetting
import com.github.sorusclient.client.setting.DisplayedSetting.Toggle
import com.github.sorusclient.client.setting.Setting

open class ModuleDisableable(id: String) : Module(id) {

    private var enabled: Setting<Boolean>

    init {
        register("enabled", Setting(false).also { enabled = it })
    }

    fun setEnabled(enabled: Boolean) {
        this.enabled.realValue = enabled
    }

    fun isEnabled(): Boolean {
        return enabled.value
    }

    override fun addSettings(settings: MutableList<DisplayedSetting>) {
        super.addSettings(settings)
        settings.add(Toggle("Enabled", enabled))
    }

}