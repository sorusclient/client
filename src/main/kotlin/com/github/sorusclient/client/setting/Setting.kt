package com.github.sorusclient.client.setting

class Setting<T>(val type: Class<T>, defaultValue: T) {

    var realValue: T
    var forcedValues: List<T>? = null
        private set

    val value: T
        get() {
            return if (forcedValues != null) {
                if (forcedValues!!.contains(realValue)) realValue else forcedValues!![0]
            } else {
                realValue
            }
        }

        constructor(defaultValue: T) : this(defaultValue!!::class.java as Class<T>, defaultValue)

        init {
            realValue = defaultValue
        }

        fun setForcedValueRaw(forcedValues: List<Any>?) {
            this.forcedValues = forcedValues as List<T>?
        }

        val isForcedValue: Boolean
        get() = forcedValues != null

        fun setValueRaw(value: Any) {
            realValue = value as T
        }

}