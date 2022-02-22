package com.github.sorusclient.client.setting.display

import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.setting.Setting
import com.github.sorusclient.client.util.Color

sealed class DisplayedSetting: Displayed() {

    open class ConfigurableDataSingleSetting<T>(val setting: Setting<T>, val displayName: String) : DisplayedSetting() {
        override val name: String
            get() = displayName
    }

    class Toggle(setting: Setting<Boolean>, displayName: String): ConfigurableDataSingleSetting<Boolean>(setting, displayName)

    class Slider(setting: Setting<out Number>, displayName: String, val minimum: Double, val maximum: Double): ConfigurableDataSingleSetting<Number>(setting as Setting<Number>, displayName)

    class ClickThrough(setting: Setting<out Enum<*>>, displayName: String): ConfigurableDataSingleSetting<Enum<*>>(setting as Setting<Enum<*>>, displayName)

    class KeyBind(setting: Setting<Key>, displayName: String): ConfigurableDataSingleSetting<Key>(setting, displayName)

    class ColorPicker(setting: Setting<Color>, displayName: String): ConfigurableDataSingleSetting<Color>(setting, displayName)

    class Dependent<T>(val configurableData: DisplayedSetting, val setting: Setting<T>, expectedValue: T) : DisplayedSetting() {
        val expectedValue: T

        override val name: String
            get() = configurableData.name

        init {
            this.expectedValue = expectedValue
        }

    }

}