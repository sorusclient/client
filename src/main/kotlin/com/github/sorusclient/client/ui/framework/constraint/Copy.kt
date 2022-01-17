package com.github.sorusclient.client.ui.framework.constraint

import com.github.sorusclient.client.ui.framework.Component
import com.github.sorusclient.client.util.Color

class Copy @JvmOverloads constructor(private val multiplier: Double = 1.0) : Constraint {
    override fun getXValue(componentRuntime: Component.Runtime): Double {
        return 0.0
    }

    override fun getYValue(componentRuntime: Component.Runtime): Double {
        return 0.0
    }

    override fun getWidthValue(componentRuntime: Component.Runtime): Double {
        return componentRuntime.height * multiplier
    }

    override fun getHeightValue(componentRuntime: Component.Runtime): Double {
        return componentRuntime.width * multiplier
    }

    override fun getCornerRadiusValue(componentRuntime: Component.Runtime): Double {
        return 0.0
    }

    override fun getPaddingValue(componentRuntime: Component.Runtime): Double {
        return 0.0
    }

    override fun getColorValue(componentRuntime: Component.Runtime): Color {
        return null!!
    }

    override fun getStringValue(componentRuntime: Component.Runtime): String {
        return null!!
    }
}