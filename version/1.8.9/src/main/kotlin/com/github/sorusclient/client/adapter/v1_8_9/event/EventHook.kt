/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9.event

import com.github.sorusclient.client.adapter.event.*
import com.github.sorusclient.client.adapter.v1_8_9.Util
import com.github.sorusclient.client.event.call
import v1_8_9.org.lwjgl.input.Keyboard
import v1_8_9.org.lwjgl.input.Mouse
import v1_8_9.org.lwjgl.opengl.Display
import v1_8_9.org.lwjgl.opengl.GL11
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.gui.screen.Screen
import v1_8_9.net.minecraft.client.util.Window
import v1_8_9.net.minecraft.client.world.ClientWorld
import v1_8_9.net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
import v1_8_9.net.minecraft.text.Text

object EventHook {

    @JvmStatic
    @Suppress("Unused")
    fun onRender() {
        val textureEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D)
        val blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND)
        RenderEvent().call()
        setEnabled(GL11.GL_TEXTURE_2D, textureEnabled)
        setEnabled(GL11.GL_BLEND, blendEnabled)
    }

    @JvmStatic
    @Suppress("Unused")
    fun onInGameRender() {
        val textureEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D)
        val blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND)
        RenderInGameEvent().call()
        setEnabled(GL11.GL_TEXTURE_2D, textureEnabled)
        setEnabled(GL11.GL_BLEND, blendEnabled)
    }

    private fun setEnabled(capability: Int, enabled: Boolean) {
        when (capability) {
            GL11.GL_BLEND -> {
                if (enabled) {
                    GlStateManager.enableBlend()
                } else {
                    GlStateManager.disableBlend()
                }
            }
            GL11.GL_TEXTURE_2D -> {
                if (enabled) {
                    GlStateManager.enableTexture()
                } else {
                    GlStateManager.disableTexture()
                }
            }
        }
    }

    @JvmStatic
    @Suppress("Unused")
    fun onKey() {
        val key = Keyboard.getEventKey()
        val pressed = Keyboard.getEventKeyState()
        val repeat = Keyboard.isRepeatEvent()
        KeyEvent(Util.getKey(key), pressed, repeat).call()

        var string = "0123456789abcdefghijklmnoppqrstuvwxyz .,/"
        string += string.uppercase()

        if (string.contains(Keyboard.getEventCharacter())) {
            KeyCharEvent(Keyboard.getEventCharacter()).call()
        }
    }

    @JvmStatic
    @Suppress("Unused")
    fun onMouse() {
        val button = Mouse.getEventButton()
        val pressed = Mouse.getEventButtonState()
        val x = Mouse.getEventX()
        val y = Mouse.getEventY()
        val window = Window(MinecraftClient.getInstance())

        MouseEvent(
            Util.getButton(button),
            pressed,
            x / Display.getWidth().toDouble() * window.scaledWidth,
            window.scaledHeight - y / Display.getHeight().toDouble() * window.scaledHeight,
            Mouse.getDWheel() / 120.0
        ).call()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onConnect(world: ClientWorld?, ip: String) {
        if (world == null && ip.isEmpty()) {
            GameLeaveEvent().call()
        }
    }

    @JvmStatic
    @Suppress("Unused")
    fun onCustomPayload(packet: CustomPayloadS2CPacket) {
        var channel = packet.channel
        if (channel.startsWith("sorus:")) {
            channel = channel.substring(6)
            SorusCustomPacketEvent(channel, packet.payload.readString(32767)).call()
        }
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGameJoin() {
        GameJoinEvent().call()
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
    fun onBossBarRender(): BossBarRenderEvent {
        val event = BossBarRenderEvent()
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
    fun onHotBarRender(): HotBarRenderEvent {
        val event = HotBarRenderEvent()
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
    fun onSideBarRender(): SideBarRenderEvent {
        val event = SideBarRenderEvent()
        event.call()
        return event
    }

    @JvmStatic
    @Suppress("Unused")
    fun onChatReceived(text: Text): Text {
        val event = ChatReceivedEvent(text.asFormattedString(), Util.textToApiText(text))
        event.call()
        return Util.apiTextToText(event.text)
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGetClientBrand(brand: String): String {
        val event = GetClientBrandEvent(brand)
        event.call()
        return event.brand
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGetFOV(fov: Float): Float {
        val event = GetFOVEvent(fov.toDouble())
        event.call()
        return event.fov.toFloat()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGetSensitivity(sensitivity: Float): Float {
        val event = GetSensitivityEvent(sensitivity.toDouble())
        event.call()
        return event.sensitivity.toFloat()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGetUseCinematicCamera(useCinematicCamera: Boolean): Boolean {
        val event = GetUseCinematicCameraEvent(useCinematicCamera)
        event.call()
        return event.isUseCinematicCamera
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
    fun onInitialize() {
        val event = InitializeEvent()
        event.call()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onTick() {
        val event = TickEvent()
        event.call()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onOpenScreen(screen: Any) {
        val event = OpenScreenEvent(Util.screenToScreenType(screen as Screen))
        event.call()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onRenderCrosshair(): Boolean {
        val event = RenderCrosshairEvent()
        event.call()
        return event.isCanceled
    }

}