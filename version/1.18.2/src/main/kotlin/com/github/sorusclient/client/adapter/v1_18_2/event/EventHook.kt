/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2.event

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Button
import com.github.sorusclient.client.adapter.event.*
import com.github.sorusclient.client.adapter.v1_18_2.Util
import com.github.sorusclient.client.event.call
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem
import v1_18_2.net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
import v1_18_2.org.lwjgl.opengl.GL11
import v1_18_2.org.lwjgl.opengl.GL11C

object EventHook {

    @JvmStatic
    @Suppress("Unused")
    fun onInGameRender() {
        val blendEnabled = GL11C.glIsEnabled(GL11C.GL_BLEND)
        RenderInGameEvent().call()
        setEnabled(GL11C.GL_BLEND, blendEnabled)
    }

    private fun setEnabled(capability: Int, enabled: Boolean) {
        when (capability) {
            GL11C.GL_BLEND -> {
                if (enabled) {
                    RenderSystem.enableBlend()
                } else {
                    RenderSystem.disableBlend()
                }
            }
            GL11C.GL_TEXTURE_2D -> {
                if (enabled) {
                    RenderSystem.enableTexture()
                } else {
                    RenderSystem.disableTexture()
                }
            }
        }
    }

    @JvmStatic
    @Suppress("Unused")
    fun onSideBarRender(): SideBarRenderEvent {
        val event = SideBarRenderEvent()
        event.call()
        return event
    }

    @JvmStatic
    @Suppress("Unused")
    fun onBossBarRender(): BossBarRenderEvent {
        val event = BossBarRenderEvent()
        event.call()
        return event
    }

    @JvmStatic
    @Suppress("Unused")
    fun onHotBarRender(): HotBarRenderEvent {
        val event = HotBarRenderEvent()
        event.call()
        return event
    }

    @JvmStatic
    @Suppress("Unused")
    fun onExperienceBarRender(): ExperienceBarRenderEvent {
        val event = ExperienceBarRenderEvent()
        event.call()
        return event
    }

    @JvmStatic
    @Suppress("Unused")
    fun onHealthBarRender(): HealthBarRenderEvent {
        val event = HealthBarRenderEvent()
        event.call()
        return event
    }

    @JvmStatic
    @Suppress("Unused")
    fun onArmorBarRender(): ArmorBarRenderEvent {
        val event = ArmorBarRenderEvent()
        event.call()
        return event
    }

    @JvmStatic
    @Suppress("Unused")
    fun onHungerBarRender(): HungerBarRenderEvent {
        val event = HungerBarRenderEvent()
        event.call()
        return event
    }

    @JvmStatic
    @Suppress("Unused")
    fun onKey(keyCode: Int, action: Int) {
        val pressed = action == 1 || action == 2
        val repeat = action == 2
        KeyEvent(Util.getKey(keyCode), pressed, repeat).call()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onChar(char: Int) {
        KeyCharEvent(char.toChar()).call()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onInitialize() {
        val event = InitializeEvent()
        event.call()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onRender() {
        val blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND)
        RenderEvent().call()
        setEnabled(GL11.GL_BLEND, blendEnabled)
    }

    private var pressedButton: Button? = null
    private var pressed = false

    @JvmStatic
    @Suppress("Unused")
    fun onMousePress(buttonCode: Int, state: Int) {
        val pressed = state == 1
        val x = AdapterManager.adapter.mouseLocation[0]
        val y = AdapterManager.adapter.mouseLocation[1]
        MouseEvent(Util.getButton(buttonCode), pressed, x, y, 0.0).call()

        pressedButton = Util.getButton(buttonCode)
        this.pressed = pressed
    }

    @JvmStatic
    @Suppress("Unused")
    fun onMouseMove() {
        val x = AdapterManager.adapter.mouseLocation[0]
        val y = AdapterManager.adapter.mouseLocation[1]
        MouseEvent(Button.NONE, false, x, y, 0.0).call()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onMouseScroll(amount: Double) {
        val x = AdapterManager.adapter.mouseLocation[0]
        val y = AdapterManager.adapter.mouseLocation[1]
        MouseEvent(Button.UNKNOWN, false, x, y, amount).call()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onTick() {
        val event = TickEvent()
        event.call()
    }

    @JvmStatic
    var lastBoundArray = 0

    @JvmStatic
    @Suppress("Unused")
    fun onBindVertexArray(array: Int) {
        lastBoundArray = array
    }

    var lastBoundBuffer = 0
    var lastBoundBufferTarget = 0
    var lastBoundProgram = 0

    @JvmStatic
    @Suppress("Unused")
    fun onBindBuffer(target: Int, buffer: Int) {
        lastBoundBufferTarget = target
        lastBoundBuffer = buffer
    }

    @JvmStatic
    @Suppress("Unused")
    fun onUseProgram(program: Int) {
        lastBoundProgram = program
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGetFOV(fov: Double): Double {
        val event = GetFOVEvent(fov)
        event.call()
        return event.fov
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGetSensitivity(sensitivity: Double): Double {
        val event = GetSensitivityEvent(sensitivity)
        event.call()
        return event.sensitivity
    }

    @JvmStatic
    @Suppress("Unused")
    fun updateTitle(title: String): Boolean {
        return title.contains("Sorus")
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGetUseCinematicCamera(useCinematicCamera: Boolean): Boolean {
        val event = GetUseCinematicCamera(useCinematicCamera)
        event.call()
        return event.useCinematicCamera
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGetGamma(gamma: Float): Float {
        val event = GetGammaEvent(gamma.toDouble())
        event.call()
        return event.gamma.toFloat()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onCustomPayload(packet: CustomPayloadS2CPacket) {
        if (packet.channel.namespace.equals("sorus")) {
            SorusCustomPacketEvent(packet.channel.path, packet.data.readString(32767)).call()
        }
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGameJoin() {
        GameJoinEvent().call()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onDisconnect() {
        GameLeaveEvent().call()
    }

}