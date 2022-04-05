/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.ui.framework.constraint

import com.github.sorusclient.client.ui.framework.Component
import com.github.sorusclient.client.util.Color

interface Constraint {
    fun getXValue(componentRuntime: Component.Runtime): Double
    fun getYValue(componentRuntime: Component.Runtime): Double
    fun getWidthValue(componentRuntime: Component.Runtime): Double
    fun getHeightValue(componentRuntime: Component.Runtime): Double
    fun getCornerRadiusValue(componentRuntime: Component.Runtime): Double
    fun getPaddingValue(componentRuntime: Component.Runtime): Double
    fun getColorValue(componentRuntime: Component.Runtime): Color
    fun getStringValue(componentRuntime: Component.Runtime?): String?
}