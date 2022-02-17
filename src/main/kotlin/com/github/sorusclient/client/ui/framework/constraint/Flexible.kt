package com.github.sorusclient.client.ui.framework.constraint

import com.github.sorusclient.client.ui.framework.Component
import com.github.sorusclient.client.util.Color

class Flexible : Constraint {
    override fun getXValue(componentRuntime: Component.Runtime): Double {
        return 0.0
    }

    override fun getYValue(componentRuntime: Component.Runtime): Double {
        return 0.0
    }

    override fun getWidthValue(componentRuntime: Component.Runtime): Double {
        val placedComponents = componentRuntime.getParent()!!.placedComponents
        var maxLeft = -Double.MAX_VALUE
        var maxRight = Double.MAX_VALUE
        for (component in placedComponents) {
            val padding = componentRuntime.padding.coerceAtLeast(component.second[4])
            val intersectingY =
                componentRuntime.y + componentRuntime.height / 2 + padding > component.second[1] - component.second[3] / 2 + 1 && componentRuntime.y - componentRuntime.height / 2 + 1 < component.second[1] + component.second[3] / 2 + padding
            val canInteractLeft = component.second[0] < componentRuntime.x
            if (intersectingY && canInteractLeft) {
                val componentRight = component.second[0] + component.second[2] / 2 + padding
                maxLeft = maxLeft.coerceAtLeast(componentRight)
            }
            val canInteractRight = component.second[0] > componentRuntime.x
            if (intersectingY && canInteractRight) {
                val componentLeft = component.second[0] - component.second[2] / 2 - padding
                maxRight = maxRight.coerceAtMost(componentLeft)
            }
        }
        return 0.0.coerceAtLeast(maxRight - maxLeft)
    }

    override fun getHeightValue(componentRuntime: Component.Runtime): Double {
        val placedComponents = componentRuntime.getParent()!!.placedComponents
        var maxTop = -Double.MAX_VALUE
        var maxBottom = Double.MAX_VALUE
        for (component in placedComponents) {
            val padding = componentRuntime.padding.coerceAtLeast(component.second[4])
            val intersectingX =
                componentRuntime.x + componentRuntime.width / 2 + padding > component.second[0] - component.second[2] / 2 + 1 && componentRuntime.x - componentRuntime.width / 2 + 1 < component.second[0] + component.second[2] / 2 + padding
            val canInteractTop = component.second[1] < componentRuntime.y
            if (intersectingX && canInteractTop) {
                val componentBottom = component.second[1] + component.second[3] / 2 + padding
                maxTop = maxTop.coerceAtLeast(componentBottom)
            }
            val canInteractBottom = component.second[1] > componentRuntime.y
            if (intersectingX && canInteractBottom) {
                val componentTop = component.second[1] - component.second[3] / 2 - padding
                maxBottom = maxBottom.coerceAtMost(componentTop)
            }
        }
        return 0.0.coerceAtLeast(maxBottom - maxTop)
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
}