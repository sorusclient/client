package com.github.sorusclient.client.feature.impl.togglesprintsneak

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.IKeyBind
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.Category
import com.github.sorusclient.client.setting.SettingConfigure.*
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
        SettingManager.settingsCategory
            .apply {
                put("toggleSprintSneak", HashMap<String, Any>()
                    .apply {
                        put("enabled", Setting(false).also { enabled = it })
                        put("toggleSprint", Setting(false).also { toggleSprint = it })
                        put("useCustomSprintKey", Setting(false).also { useCustomSprintKey = it })
                        put("customSprintKey", Setting(Key.SHIFT_LEFT).also { customSprintKey = it })
                        put("toggleSneak", Setting(false).also { toggleSneak = it })
                        put("useCustomSneakKey", Setting(false).also { useCustomSneakKey = it })
                        put("customSneakKey", Setting(Key.SHIFT_LEFT).also { customSneakKey = it })
                    })
            }

        SettingManager.mainUICategory
            .apply {
                add(Category("Toggle Sprint & Sneak"))
                    .apply {
                        add(Toggle(enabled, "Enabled"))
                        add(Toggle(toggleSprint, "Toggle Sprint"))
                        add(Dependent(Toggle(useCustomSprintKey, "Use Custom Sprint Key"), toggleSprint, true))
                        add(Dependent(Dependent(KeyBind(customSprintKey, "Custom Sprint Key"), toggleSprint, true), useCustomSprintKey, true))
                        add(Toggle(toggleSneak, "Toggle Sneak"))
                        add(Dependent(Toggle(useCustomSneakKey, "Use Custom Sneak Key"), toggleSneak, true))
                        add(Dependent(Dependent(KeyBind(customSneakKey, "Custom Sneak Key"), toggleSneak, true), useCustomSneakKey, true))
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