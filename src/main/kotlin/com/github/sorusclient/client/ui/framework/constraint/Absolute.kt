package com.github.sorusclient.client.ui.framework.constraint

import com.github.sorusclient.client.ui.framework.Component
import com.github.sorusclient.client.util.Color

class Absolute private constructor(private val value: Any?) : Constraint {
    constructor(value: Double) : this(value as Any)
    constructor(value: Color) : this(value as Any)
    constructor(value: String?) : this(value as Any?)

    override fun getXValue(componentRuntime: Component.Runtime): Double {
        return value as Double
    }

    override fun getYValue(componentRuntime: Component.Runtime): Double {
        return value as Double
    }

    override fun getWidthValue(componentRuntime: Component.Runtime): Double {
        return value as Double
    }

    override fun getHeightValue(componentRuntime: Component.Runtime): Double {
        return value as Double
    }

    override fun getCornerRadiusValue(componentRuntime: Component.Runtime): Double {
        return value as Double
    }

    override fun getPaddingValue(componentRuntime: Component.Runtime): Double {
        return value as Double
    }

    override fun getColorValue(componentRuntime: Component.Runtime): Color {
        return value as Color
    }

    override fun getStringValue(componentRuntime: Component.Runtime?): String? {
        return value as String?
    }
}