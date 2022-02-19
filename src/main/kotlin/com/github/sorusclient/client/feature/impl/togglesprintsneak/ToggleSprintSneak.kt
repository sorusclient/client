package com.github.sorusclient.client.feature.impl.togglesprintsneak

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.IKeyBind
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.DisplayedCategory
import com.github.sorusclient.client.setting.DisplayedSetting.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager

class ToggleSprintSneak {

    private val enabled: Setting<Boolean>
    private val toggleSprint: Setting<Boolean>
    private val useCustomSprintKey: Setting<Boolean>
    private val customSprintKey: Setting<Key>
    private val toggleSneak: Setting<Boolean>
    private val useCustomSneakKey: Setting<Boolean>
    private val customSneakKey: Setting<Key>
    private var sprintToggled = false
    private var sneakToggled = false

    init {
        SettingManager.mainCategory
            .apply {
                registerDisplayed(DisplayedCategory("Toggle Sprint & Sneak"))
                    .apply {
                        registerDisplayed(Toggle("Enabled", Setting(false).also { enabled = it }))
                        registerDisplayed(Toggle("Toggle Sprint", Setting(false).also { toggleSprint = it }))
                        registerDisplayed(Dependent(Toggle("Use Custom Sprint Key", Setting(false).also { useCustomSprintKey = it }), toggleSprint, true))
                        registerDisplayed(Dependent(Dependent(KeyBind("Custom Sprint Key", Setting(Key.SHIFT_LEFT).also { customSprintKey = it }), toggleSprint, true), useCustomSprintKey, true))
                        registerDisplayed(Toggle("Toggle Sneak", Setting(false).also { toggleSneak = it }))
                        registerDisplayed(Dependent(Toggle("Use Custom Sneak Key", Setting(false).also { useCustomSneakKey = it }), toggleSneak, true))
                        registerDisplayed(Dependent(Dependent(KeyBind("Custom Sneak Key", Setting(Key.SHIFT_LEFT).also { customSneakKey = it }), toggleSneak, true), useCustomSneakKey, true))
                    }
            }

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

    fun isEnabled(): Boolean {
        return enabled.value
    }

    fun isSprintToggledValue(): Boolean {
        return isEnabled() && toggleSprint.value && sprintToggled
    }

    fun isSneakToggledValue(): Boolean {
        return isEnabled() && toggleSneak.value && sneakToggled
    }
}