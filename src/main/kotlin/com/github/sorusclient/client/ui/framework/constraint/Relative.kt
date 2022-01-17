package com.github.sorusclient.client.ui.framework.constraint

import com.github.sorusclient.client.ui.framework.Component
import com.github.sorusclient.client.util.Color

class Relative @JvmOverloads constructor(private val percent: Double, private val otherSide: Boolean = false) :
    Constraint {
    override fun getXValue(componentRuntime: Component.Runtime): Double {
        return componentRuntime.getParent()!!.width * percent
    }

    override fun getYValue(componentRuntime: Component.Runtime): Double {
        return componentRuntime.getParent()!!.height * percent
    }

    override fun getWidthValue(componentRuntime: Component.Runtime): Double {
        val size = if (otherSide) componentRuntime.getParent()!!
            .height else componentRuntime.getParent()!!.width
        return size * percent
    }

    override fun getHeightValue(componentRuntime: Component.Runtime): Double {
        val size = if (otherSide) componentRuntime.getParent()!!
            .width else componentRuntime.getParent()!!.height
        return size * percent
    }

    override fun getPaddingValue(componentRuntime: Component.Runtime): Double {
        val size = if (otherSide) componentRuntime.getParent()!!
            .height else componentRuntime.getParent()!!.width
        return size * percent
    }

    override fun getColorValue(componentRuntime: Component.Runtime): Color {
        return null!!
    }

    override fun getStringValue(componentRuntime: Component.Runtime): String {
        return null!!
    }

    override fun getCornerRadiusValue(componentRuntime: Component.Runtime): Double {
        return getPaddingValue(componentRuntime)
    }
}