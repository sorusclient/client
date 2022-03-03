package com.github.sorusclient.client.adapter.v1_8_9.event

import com.github.sorusclient.client.adapter.event.*
import com.github.sorusclient.client.adapter.v1_8_9.Util
import com.github.sorusclient.client.event.EventManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import v1_8_9.com.mojang.blaze3d.platform.GlStateManager
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.gui.screen.Screen
import v1_8_9.net.minecraft.client.util.Window
import v1_8_9.net.minecraft.client.world.ClientWorld
import v1_8_9.net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
import v1_8_9.net.minecraft.text.Text
import v1_8_9.net.minecraft.util.math.Box

object EventHook {

    @JvmStatic
    @Suppress("Unused")
    fun onRender() {
        val textureEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D)
        val blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND)
        EventManager.call(RenderEvent())
        setEnabled(GL11.GL_TEXTURE_2D, textureEnabled)
        setEnabled(GL11.GL_BLEND, blendEnabled)
    }

    @JvmStatic
    @Suppress("Unused")
    fun onInGameRender() {
        val textureEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D)
        val blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND)
        EventManager.call(RenderInGameEvent())
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
        EventManager.call(KeyEvent(Util.getKey(key), pressed, repeat))

        var string = "0123456789abcdefghijklmnoppqrstuvwxyz "
        string += string.uppercase()

        if (string.contains(Keyboard.getEventCharacter())) {
            EventManager.call(KeyCharEvent(Keyboard.getEventCharacter()))
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

        EventManager.call(
            MouseEvent(
                Util.getButton(button),
                pressed,
                x / Display.getWidth().toDouble() * window.scaledWidth,
                window.scaledHeight - y / Display.getHeight().toDouble() * window.scaledHeight,
                Mouse.getDWheel() / 120.0
            )
        )
    }

    @JvmStatic
    @Suppress("Unused")
    fun onConnect(world: ClientWorld?, ip: String) {
        if (world == null && ip.isEmpty()) {
            EventManager.call(GameLeaveEvent())
        }
    }

    @JvmStatic
    @Suppress("Unused")
    fun onCustomPayload(packet: CustomPayloadS2CPacket) {
        var channel = packet.channel
        if (channel.startsWith("sorus:")) {
            channel = channel.substring(6)
            EventManager
                .call(SorusCustomPacketEvent(channel, packet.payload.readString(32767)))
        }
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGameJoin() {
        EventManager.call(GameJoinEvent())
    }

    @JvmStatic
    @Suppress("Unused")
    fun onArmorBarRender(): ArmorBarRenderEvent {
        val event = ArmorBarRenderEvent()
        EventManager.call(event)
        return event
    }

    @JvmStatic
    @Suppress("Unused")
    fun onBossBarRender(): BossBarRenderEvent {
        val event = BossBarRenderEvent()
        EventManager.call(event)
        return event
    }

    @JvmStatic
    @Suppress("Unused")
    fun onExperienceBarRender(): ExperienceBarRenderEvent {
        val event = ExperienceBarRenderEvent()
        EventManager.call(event)
        return event
    }

    @JvmStatic
    @Suppress("Unused")
    fun onHealthBarRender(): HealthBarRenderEvent {
        val event = HealthBarRenderEvent()
        EventManager.call(event)
        return event
    }

    @JvmStatic
    @Suppress("Unused")
    fun onHotBarRender(): HotBarRenderEvent {
        val event = HotBarRenderEvent()
        EventManager.call(event)
        return event
    }

    @JvmStatic
    @Suppress("Unused")
    fun onHungerBarRender(): HungerBarRenderEvent {
        val event = HungerBarRenderEvent()
        EventManager.call(event)
        return event
    }

    @JvmStatic
    @Suppress("Unused")
    fun onSideBarRender(): SideBarRenderEvent {
        val event = SideBarRenderEvent()
        EventManager.call(event)
        return event
    }

    /*@JvmStatic
    @Suppress("Unused")
    fun onBlockOutlineRender(box: Box): BlockOutlineRenderEvent {
        val event = BlockOutlineRenderEvent(
            com.github.sorusclient.client.adapter.Box(
                box.minX,
                box.maxX,
                box.minY,
                box.maxY,
                box.minZ,
                box.maxZ
            )
        )
        EventManager.call(event)
        return event
    }*/

    @JvmStatic
    @Suppress("Unused")
    fun onChatReceived(text: Text): Text {
        val event = ChatReceivedEvent(text.asFormattedString(), Util.textToApiText(text))
        EventManager.call(event)
        return Util.apiTextToText(event.text)
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGetClientBrand(brand: String): String {
        val event = GetClientBrandEvent(brand)
        EventManager.call(event)
        return event.brand
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGetFOV(fov: Float): Float {
        val event = GetFOVEvent(fov.toDouble())
        EventManager.call(event)
        return event.fov.toFloat()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGetSensitivity(sensitivity: Float): Float {
        val event = GetSensitivityEvent(sensitivity.toDouble())
        EventManager.call(event)
        return event.sensitivity.toFloat()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGetUseCinematicCamera(useCinematicCamera: Boolean): Boolean {
        val event = GetUseCinematicCamera(useCinematicCamera)
        EventManager.call(event)
        return event.useCinematicCamera
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGetGamma(gamma: Float): Float {
        val event = GetGammaEvent(gamma.toDouble())
        EventManager.call(event)
        return event.gamma.toFloat()
    }

    @JvmStatic
    @Suppress("Unused")
    fun onInitialize() {
        val event = InitializeEvent()
        EventManager.call(event)
    }

    @JvmStatic
    @Suppress("Unused")
    fun onTick() {
        val event = TickEvent()
        EventManager.call(event)
    }

    @JvmStatic
    @Suppress("Unused")
    fun onOpenScreen(screen: Any) {
        val event = OpenScreenEvent(Util.screenToScreenType(screen as Screen))
        EventManager.call(event)
    }

    @JvmStatic
    @Suppress("Unused")
    fun onRenderCrosshair(): Boolean {
        val event = RenderCrosshairEvent()
        EventManager.call(event)
        return event.canceled
    }

}