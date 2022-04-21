/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.zoom.v1_8_9

import com.github.sorusclient.client.feature.impl.zoom.Zoom

object ZoomHook {

    @JvmStatic
    fun onHotBarScroll(amount: Int): Int {
        return if (Zoom.isScrollZoom()) { 0 } else { amount }
    }

}