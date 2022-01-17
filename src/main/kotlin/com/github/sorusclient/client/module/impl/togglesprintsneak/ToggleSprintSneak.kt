package com.github.sorusclient.client.module.impl.togglesprintsneak

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.IKeyBind
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.module.ModuleDisableable
import com.github.sorusclient.client.setting.ConfigurableData
import com.github.sorusclient.client.setting.ConfigurableData.KeyBind
import com.github.sorusclient.client.setting.ConfigurableData.Toggle
import com.github.sorusclient.client.setting.Setting

class ToggleSprintSneak : ModuleDisableable("toggleSprintSneak") {
    private var toggleSprint: Setting<Boolean>
    private var useCustomSprintKey: Setting<Boolean>
    private var customSprintKey: Setting<Key>
    private var toggleSneak: Setting<Boolean>
    private var useCustomSneakKey: Setting<Boolean>
    private var customSneakKey: Setting<Key>
    private var sprintToggled = false
    private var sneakToggled = false

    init {
        register("toggleSprint", Setting(false).also { toggleSprint = it })
        register("useCustomSprintKey", Setting(false).also { useCustomSprintKey = it })
        register("customSprintKey", Setting(Key.SHIFT_LEFT).also { customSprintKey = it })
        register("toggleSneak", Setting(false).also { toggleSneak = it })
        register("useCustomSneakKey", Setting(false).also { useCustomSneakKey = it })
        register("customSneakKey", Setting(Key.CONTROL_LEFT).also { customSneakKey = it })
        EventManager.register(this::onKey)
    }

    private fun onKey(event: KeyEvent) {
        if (event.isPressed && !event.isRepeat) {
            if (event.key == sprintKey) {
                sprintToggled = !sprintToggled
            }
            if (event.key == sneakKey) {
                sneakToggled = !sneakToggled
            }
        }
    }

    private val sprintKey: Key
        get() {
            val adapter = AdapterManager.getAdapter()
            return if (useCustomSprintKey.value) customSprintKey.value else adapter.getKeyBind(IKeyBind.KeyBindType.SPRINT).key
        }
    private val sneakKey: Key
        get() {
            val adapter = AdapterManager.getAdapter()
            return if (useCustomSneakKey.value) customSneakKey.value else adapter.getKeyBind(IKeyBind.KeyBindType.SNEAK).key
        }

    fun isSprintToggledValue(): Boolean {
        return isEnabled() && toggleSprint.value && sprintToggled
    }

    fun isSneakToggledValue(): Boolean {
        return isEnabled() && toggleSneak.value && sneakToggled
    }

    override fun addSettings(settings: MutableList<ConfigurableData>) {
        super.addSettings(settings)
        settings.add(Toggle("Toggle Sprint", toggleSprint))
        settings.add(
            ConfigurableData.Dependent(
                Toggle("Use Custom Sprint Key", useCustomSprintKey),
                toggleSprint,
                true
            )
        )
        settings.add(
            ConfigurableData.Dependent(
                ConfigurableData.Dependent(
                    KeyBind(
                        "Custom Sprint Key",
                        customSprintKey
                    ), useCustomSprintKey, true
                ), toggleSprint, true
            )
        )
        settings.add(Toggle("Toggle Sneak", toggleSneak))
        settings.add(ConfigurableData.Dependent(Toggle("Use Custom Sneak Key", useCustomSneakKey), toggleSneak, true))
        settings.add(
            ConfigurableData.Dependent(
                ConfigurableData.Dependent(
                    KeyBind("Custom Sneak Key", customSneakKey),
                    useCustomSneakKey,
                    true
                ), toggleSneak, true
            )
        )
    }
}