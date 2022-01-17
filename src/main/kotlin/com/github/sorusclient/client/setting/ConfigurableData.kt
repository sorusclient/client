package com.github.sorusclient.client.setting

import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.util.Color

sealed class ConfigurableData {

    open class ConfigurableDataSingleSetting<T>(val displayName: String, val setting: Setting<T>) : ConfigurableData()

    class Toggle(displayName: String, setting: Setting<Boolean>) :
        ConfigurableDataSingleSetting<Boolean>(displayName, setting)

    class Slider(displayName: String, setting: Setting<out Number>, val minimum: Double, val maximum: Double) :
        ConfigurableDataSingleSetting<Number>(displayName, setting as Setting<Number>)

    class ClickThrough(displayName: String, setting: Setting<out Enum<*>>) :
        ConfigurableDataSingleSetting<Enum<*>>(displayName, setting as Setting<Enum<*>>)

    class KeyBind(displayName: String, setting: Setting<Key>) :
        ConfigurableDataSingleSetting<Key>(displayName, setting)

    class ColorPicker(displayName: String, setting: Setting<Color>) :
        ConfigurableDataSingleSetting<Color>(displayName, setting)

    class Dependent<T>(val configurableData: ConfigurableData, setting: Setting<T>, expectedValue: T) : ConfigurableData() {
        val setting: Setting<*>
        val expectedValue: T

        init {
            this.setting = setting
            this.expectedValue = expectedValue
        }
    }

}