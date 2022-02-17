package com.github.sorusclient.client.ui.framework.constraint

import com.github.sorusclient.client.ui.framework.Component
import com.github.sorusclient.client.util.Color

class Side(private val side: Int) : Constraint {
    override fun getXValue(componentRuntime: Component.Runtime): Double {
        val placedComponents = componentRuntime.getParent()!!.placedComponents
        var maxLeft = -Double.MAX_VALUE
        var maxRight = Double.MAX_VALUE
        for (component in placedComponents) {
            val padding = componentRuntime.padding.coerceAtLeast(component.second[4])
            val intersectingY =
                componentRuntime.y + componentRuntime.height / 2 + padding > component.second[1] - component.second[3] / 2 + 1 && componentRuntime.y - componentRuntime.height / 2 + 1 < component.second[1] + component.second[3] / 2 + padding
            val canInteractLeft = component.second[0] < componentRuntime.x + componentRuntime.width / 2
            if (intersectingY && canInteractLeft) {
                val componentRight = component.second[0] + component.second[2] / 2 + padding
                maxLeft = maxLeft.coerceAtLeast(componentRight)
            }
            val canInteractRight = component.second[0] > componentRuntime.x - componentRuntime.width / 2
            if (intersectingY && canInteractRight) {
                val componentLeft = component.second[0] - component.second[2] / 2 - padding
                maxRight = maxRight.coerceAtMost(componentLeft)
            }
        }
        when (side) {
            -1 -> return maxLeft + componentRuntime.width / 2
            0 -> return (maxLeft + maxRight) / 2
            1 -> return maxRight - componentRuntime.width / 2
        }
        return 0.0
    }

    override fun getYValue(componentRuntime: Component.Runtime): Double {
        val placedComponents = componentRuntime.getParent()!!.placedComponents
        var maxTop = -Double.MAX_VALUE
        var maxBottom = Double.MAX_VALUE
        for (component in placedComponents) {
            val padding = componentRuntime.padding.coerceAtLeast(component.second[4])
            val intersectingX =
                componentRuntime.x + componentRuntime.width / 2 + padding > component.second[0] - component.second[2] / 2 + 1 && componentRuntime.x - componentRuntime.width / 2 + 1 < component.second[0] + component.second[2] / 2 + padding
            val canInteractTop = component.second[1] < componentRuntime.y + componentRuntime.height / 2
            if (intersectingX && canInteractTop) {
                val componentBottom = component.second[1] + component.second[3] / 2 + padding
                maxTop = maxTop.coerceAtLeast(componentBottom)
            }
            val canInteractBottom = component.second[1] > componentRuntime.y - componentRuntime.height / 2
            if (intersectingX && canInteractBottom) {
                val componentTop = component.second[1] - component.second[3] / 2 - padding
                maxBottom = maxBottom.coerceAtMost(componentTop)
            }
        }
        when (side) {
            -1 -> return maxTop + componentRuntime.height / 2
            0 -> return (maxTop + maxBottom) / 2
            1 -> return maxBottom - componentRuntime.height / 2
        }
        return 0.0
    }

    override fun getWidthValue(componentRuntime: Component.Runtime): Double {
        return 0.0
    }

    override fun getHeightValue(componentRuntime: Component.Runtime): Double {
        return 0.0
    }

    override fun getPaddingValue(componentRuntime: Component.Runtime): Double {
        return 0.0
    }

    override fun getColorValue(componentRuntime: Component.Runtime): Color {
        return null!!
    }

    override fun getStringValue(componentRuntime: Component.Runtime?): String {
        return null!!
    }

    override fun getCornerRadiusValue(componentRuntime: Component.Runtime): Double {
        return 0.0
    }

    companion object {
        const val POSITIVE = 1
        const val ZERO = 0
        const val NEGATIVE = -1
    }
}