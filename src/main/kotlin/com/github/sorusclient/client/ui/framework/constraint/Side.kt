package com.github.sorusclient.client.ui.framework.constraint

import com.github.sorusclient.client.ui.framework.Component
import com.github.sorusclient.client.util.Color

class Side(private val side: Int) : Constraint {

    override fun getXValue(componentRuntime: Component.Runtime): Double {
        val placedComponents = componentRuntime.getParent()!!.placedComponents
        var maxLeft = -Double.MAX_VALUE
        var maxRight = Double.MAX_VALUE
        for (component in placedComponents) {
            val bottomPadding = componentRuntime.bottomPadding.coerceAtLeast(component.second[6])
            val topPadding = componentRuntime.topPadding.coerceAtLeast(component.second[7])
            val intersectingY = componentRuntime.y + componentRuntime.height / 2 + bottomPadding > component.second[1] - component.second[3] / 2 + 1 && componentRuntime.y - componentRuntime.height / 2 + 1 < component.second[1] + component.second[3] / 2 + topPadding

            val leftPadding = componentRuntime.leftPadding.coerceAtLeast(component.second[5])
            val canInteractLeft = component.second[0] < componentRuntime.x
            if (intersectingY && canInteractLeft) {
                val componentRight = component.second[0] + component.second[2] / 2 + leftPadding
                maxLeft = maxLeft.coerceAtLeast(componentRight)
            }

            val rightPadding = componentRuntime.rightPadding.coerceAtLeast(component.second[4])
            val canInteractRight = component.second[0] > componentRuntime.x
            if (intersectingY && canInteractRight) {
                val componentLeft = component.second[0] - component.second[2] / 2 - rightPadding
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
            val rightPadding = componentRuntime.rightPadding.coerceAtLeast(component.second[4])
            val leftPadding = componentRuntime.leftPadding.coerceAtLeast(component.second[5])

            val intersectingX = componentRuntime.x + componentRuntime.width / 2 + rightPadding > component.second[0] - component.second[2] / 2 + 1 && componentRuntime.x - componentRuntime.width / 2 + 1 < component.second[0] + component.second[2] / 2 + leftPadding

            val topPadding = componentRuntime.topPadding.coerceAtLeast(component.second[7])
            val canInteractTop = component.second[1] < componentRuntime.y
            if (intersectingX && canInteractTop) {
                val componentBottom = component.second[1] + component.second[3] / 2 + topPadding
                maxTop = maxTop.coerceAtLeast(componentBottom)
            }

            val bottomPadding = componentRuntime.bottomPadding.coerceAtLeast(component.second[6])
            val canInteractBottom = component.second[1] > componentRuntime.y
            if (intersectingX && canInteractBottom) {
                val componentTop = component.second[1] - component.second[3] / 2 - bottomPadding
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