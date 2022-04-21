/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.zoom.v1_18_2

import com.github.sorusclient.client.feature.impl.zoom.IZoomHelper
import v1_18_2.net.minecraft.client.MinecraftClient

@Suppress("UNUSED")
class ZoomHelper: IZoomHelper{

    override fun onUpdateZoom() {
        MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate()
    }

}