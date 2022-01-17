package com.github.sorusclient.client.module

import com.github.sorusclient.client.setting.ConfigurableData
import com.github.sorusclient.client.setting.ConfigurableData.Toggle
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.setting.SettingContainer
import com.github.sorusclient.client.setting.Util

open class Module(override val id: String) : SettingContainer {

    private val settings: MutableMap<String, Setting<*>> = HashMap()
    private val sharedInternal: Setting<Boolean> = Setting(false)

    protected fun register(id: String, setting: Setting<*>) {
        settings[id] = setting
    }

    override fun load(settings: Map<String, Any>) {
        for ((key, value) in settings) {
            val setting1 = this.settings[key]
            Util.toJava(setting1?.type, value)?.let { setting1?.setValueRaw(it) }
        }
    }

    override fun loadForced(settings: Map<String, Any>) {
        for ((key, value) in settings) {
            val setting1 = this.settings[key]!!
            val forcedValues: MutableList<Any> = ArrayList()
            if (value is List<*>) {
                for (element in value) {
                    forcedValues.add(Util.toJava(setting1.type, element)!!)
                }
            } else {
                forcedValues.add(Util.toJava(setting1.type, value)!!)
            }
            setting1.setForcedValueRaw(forcedValues)
        }
    }

    override fun removeForced() {
        for (setting in settings.values) {
            setting.setForcedValueRaw(null)
        }
    }

    override fun save(): Map<String, Any> {
        val settingsMap: MutableMap<String, Any> = HashMap()
        for ((key, value) in settings) {
            settingsMap[key] = Util.toData(value.value!!)
        }
        return settingsMap
    }

    open fun addSettings(settings: MutableList<ConfigurableData>) {
        settings.add(Toggle("Shared", sharedInternal))
    }

    override var shared: Boolean = false

}