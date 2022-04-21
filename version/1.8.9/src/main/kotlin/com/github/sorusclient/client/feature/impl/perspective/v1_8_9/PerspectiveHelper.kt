/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.perspective.v1_8_9

import com.github.sorusclient.client.feature.impl.perspective.IPerspectiveHelper
import v1_8_9.net.minecraft.client.MinecraftClient

@Suppress("UNUSED")
class PerspectiveHelper : IPerspectiveHelper {

    override fun onToggle() {
        PerspectiveHook.yaw = MinecraftClient.getInstance().player!!.yaw
        PerspectiveHook.prevYaw = MinecraftClient.getInstance().player!!.prevYaw
        PerspectiveHook.pitch = MinecraftClient.getInstance().player!!.pitch
        PerspectiveHook.prevPitch = MinecraftClient.getInstance().player!!.prevPitch
        if (MinecraftClient.getInstance().options.perspective == 2) {
            PerspectiveHook.yaw += 180f
            PerspectiveHook.prevYaw += 180f
            PerspectiveHook.pitch = -PerspectiveHook.pitch
            PerspectiveHook.prevPitch = -PerspectiveHook.prevPitch
        }
    }

}