package com.github.sorusclient.client.feature.impl.togglesprintsneak

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.IKeyBind
import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.adapter.event.KeyEvent
import com.github.sorusclient.client.event.EventManager
import com.github.sorusclient.client.setting.display.DisplayedCategory
import com.github.sorusclient.client.setting.display.DisplayedSetting.*
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.data.SettingData

class ToggleSprintSneak {

    private val toggleSprint: Setting<Boolean>
    private val useCustomSprintKey: Setting<Boolean>
    private val customSprintKey: Setting<out MutableList<Key>>
    private val toggleSneak: Setting<Boolean>
    private val useCustomSneakKey: Setting<Boolean>
    private val customSneakKey: Setting<out MutableList<Key>>
    private var sprintToggled = false
    private var sneakToggled = false

    init {
        SettingManager.settingsCategory
            .apply {
                data["toggleSprintSneak"] = CategoryData()
                    .apply {
                        data["toggleSprint"] = SettingData(Setting(false).also { toggleSprint = it })
                        data["useCustomSprintKey"] = SettingData(Setting(false).also { useCustomSprintKey = it })
                        data["customSprintKey"] = SettingData(Setting(arrayListOf(Key.SHIFT_LEFT)).also { customSprintKey = it })
                        data["toggleSneak"] = SettingData(Setting(false).also { toggleSneak = it })
                        data["useCustomSneakKey"] = SettingData(Setting(false).also { useCustomSneakKey = it })
                        data["customSneakKey"] = SettingData(Setting(arrayListOf(Key.SHIFT_LEFT)).also { customSneakKey = it })
                    }
            }

        SettingManager.mainUICategory
            .apply {
                add(DisplayedCategory("Toggle Sprint & Sneak"))
                    .apply {
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
            return if (useCustomSprintKey.value) customSprintKey.value[0] else adapter.getKeyBind(IKeyBind.KeyBindType.SPRINT).key
        }
    private val sneakKey: Key
        get() {
            val adapter = AdapterManager.getAdapter()
            return if (useCustomSneakKey.value) customSneakKey.value[0] else adapter.getKeyBind(IKeyBind.KeyBindType.SNEAK).key
        }

    fun isSprintToggledValue(): Boolean {
        return toggleSprint.value && sprintToggled
    }

    fun isSneakToggledValue(): Boolean {
        return toggleSneak.value && sneakToggled
    }
}