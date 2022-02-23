package com.github.sorusclient.client.ui.framework.constraint

import com.github.sorusclient.client.ui.framework.Component
import com.github.sorusclient.client.util.Color
import java.util.function.Function

class Dependent(private val function: Function<Map<String, Any>, Any>) : Constraint {
    override fun getXValue(componentRuntime: Component.Runtime): Double {
        val value = function.apply(componentRuntime.availableState)
        return if (value is Constraint) {
            value.getXValue(componentRuntime)
        } else {
            value as Double
        }
    }

    override fun getYValue(componentRuntime: Component.Runtime): Double {
        val value = function.apply(componentRuntime.availableState)
        return if (value is Constraint) {
            value.getYValue(componentRuntime)
        } else {
            value as Double
        }
    }

    override fun getWidthValue(componentRuntime: Component.Runtime): Double {
        val value = function.apply(componentRuntime.availableState)
        return if (value is Constraint) {
            value.getWidthValue(componentRuntime)
        } else {
            value as Double
        }
    }

    override fun getHeightValue(componentRuntime: Component.Runtime): Double {
        val value = function.apply(componentRuntime.availableState)
        return if (value is Constraint) {
            value.getHeightValue(componentRuntime)
        } else {
            value as Double
        }
    }

    override fun getCornerRadiusValue(componentRuntime: Component.Runtime): Double {
        return 0.0
    }

    override fun getPaddingValue(componentRuntime: Component.Runtime): Double {
        return 0.0
    }

    override fun getColorValue(componentRuntime: Component.Runtime): Color {
        val value = function.apply(componentRuntime.availableState)
        return if (value is Constraint) {
            value.getColorValue(componentRuntime)
        } else {
            value as Color
        }
    }

    override fun getStringValue(componentRuntime: Component.Runtime?): String {
        return this.apply(componentRuntime) as String
    }

    private fun apply(componentRuntime: Component.Runtime?): Any {
        return function.apply(componentRuntime?.availableState!!)
    }
}