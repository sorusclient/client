package com.github.sorusclient.client.setting

import com.github.sorusclient.client.adapter.Key
import com.github.sorusclient.client.util.Color

sealed class DisplayedSetting: Displayed() {

    open class ConfigurableDataSingleSetting<T: Any>(val displayName: String, val setting: Setting<T>) : DisplayedSetting() {

        override val id: String
            get() {
                return displayName
            }

        override fun save(): Any? {
            return if (setting.overriden) {
                Util.toData(setting.realValue)
            } else {
                null
            }
        }

        override fun load(any: Any, isPrimary: Boolean) {
            Util.toJava(setting.type, any)?.let { setting.setValueRaw(it, isPrimary) }
        }

    }

    class Toggle(displayName: String, setting: Setting<Boolean>):
        ConfigurableDataSingleSetting<Boolean>(displayName, setting)

    class Slider(displayName: String, setting: Setting<out Number>, val minimum: Double, val maximum: Double):
        ConfigurableDataSingleSetting<Number>(displayName, setting as Setting<Number>)

    class ClickThrough(displayName: String, setting: Setting<out Enum<*>>):
        ConfigurableDataSingleSetting<Enum<*>>(displayName, setting as Setting<Enum<*>>)

    class KeyBind(displayName: String, setting: Setting<Key>):
        ConfigurableDataSingleSetting<Key>(displayName, setting)

    class ColorPicker(displayName: String, setting: Setting<Color>):
        ConfigurableDataSingleSetting<Color>(displayName, setting)

    class None(setting: Setting<*>) :
        ConfigurableDataSingleSetting<Any>("", setting as Setting<Any>)

    class Dependent<T>(val configurableData: DisplayedSetting, setting: Setting<T>, expectedValue: T) : DisplayedSetting() {
        val setting: Setting<*>
        val expectedValue: T

        override val id: String
            get() = configurableData.id

        init {
            this.setting = setting
            this.expectedValue = expectedValue
        }

        override fun save(): Any? {
            return configurableData.save()
        }

        override fun load(any: Any, isPrimary: Boolean) {
            configurableData.load(any, isPrimary)
        }

    }

}