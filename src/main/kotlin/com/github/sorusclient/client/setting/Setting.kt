package com.github.sorusclient.client.setting

class Setting<T>(val type: Class<T>, val defaultValue: T) {

    var realValue: T
    var forcedValues: List<T>? = null
        private set

    private var parentValue: T? = null

    var overriden = true

    val value: T
        get() {
            val realValue = if (overriden || parentValue == null) { realValue } else { parentValue!! }
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

    fun setValueRaw(value: Any, isPrimary: Boolean) {
        if (!isPrimary) {
            parentValue = value as T
        }
        overriden = isPrimary
        realValue = value as T
    }

}