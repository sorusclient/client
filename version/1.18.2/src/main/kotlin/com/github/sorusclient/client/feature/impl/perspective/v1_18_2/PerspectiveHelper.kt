/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.feature.impl.perspective.v1_18_2

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.bootstrap.Initializer
import com.github.sorusclient.client.feature.impl.perspective.IPerspectiveHelper
import v1_18_2.net.minecraft.client.MinecraftClient
import v1_18_2.net.minecraft.client.option.Perspective

class PerspectiveHelper : IPerspectiveHelper, Initializer {

    override fun initialize() {
        InterfaceManager.register(this)
    }

    override fun onToggle() {
        PerspectiveHook.yaw = MinecraftClient.getInstance().player!!.yaw
        PerspectiveHook.prevYaw = MinecraftClient.getInstance().player!!.prevYaw
        PerspectiveHook.pitch = MinecraftClient.getInstance().player!!.pitch
        PerspectiveHook.prevPitch = MinecraftClient.getInstance().player!!.prevPitch
        if (MinecraftClient.getInstance().options.perspective == Perspective.THIRD_PERSON_FRONT) {
            PerspectiveHook.yaw += 180f
            PerspectiveHook.prevYaw += 180f
            PerspectiveHook.pitch = -PerspectiveHook.pitch
            PerspectiveHook.prevPitch = -PerspectiveHook.prevPitch
        }
    }

}