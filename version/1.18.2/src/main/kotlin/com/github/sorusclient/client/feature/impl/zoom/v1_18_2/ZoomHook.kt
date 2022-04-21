/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.zoom.v1_18_2

import com.github.sorusclient.client.feature.impl.zoom.Zoom

object ZoomHook {

    @JvmStatic
    fun onHotBarScroll(amount: Double): Double {
        return if (Zoom.isScrollZoom()) { 0.0 } else { amount }
    }

}