package com.github.sorusclient.client.adapter.v1_18_2.event

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Button
import com.github.sorusclient.client.adapter.event.*
import com.github.sorusclient.client.adapter.v1_18_2.Util
import com.github.sorusclient.client.event.EventManager
import v1_18_2.org.lwjgl.opengl.GL11
import v1_18_2.org.lwjgl.opengl.GL11C
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem
import v1_18_2.net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket

object EventHook {

    @JvmStatic
    @Suppress("Unused")
    fun onInGameRender() {
        //val textureEnabled = GL11C.glIsEnabled(GL11C.GL_TEXTURE_2D)
        val blendEnabled = GL11C.glIsEnabled(GL11C.GL_BLEND)
        EventManager.call(RenderInGameEvent())
        //setEnabled(GL11C.GL_TEXTURE_2D, textureEnabled)
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
    fun onHotBarRender(): HotBarRenderEvent {
        val event = HotBarRenderEvent()
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
    fun onArmorBarRender(): ArmorBarRenderEvent {
        val event = ArmorBarRenderEvent()
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
    fun onKey(keyCode: Int, action: Int) {
        val pressed = action == 1 || action == 2
        val repeat = action == 2
        EventManager.call(KeyEvent(Util.getKey(keyCode), pressed, repeat))
    }

    @JvmStatic
    @Suppress("Unused")
    fun onChar(char: Int) {
        EventManager.call(KeyCharEvent(char.toChar()))
    }

    @JvmStatic
    @Suppress("Unused")
    fun onInitialize() {
        val event = InitializeEvent()
        EventManager.call(event)
    }

    @JvmStatic
    @Suppress("Unused")
    fun onRender() {
        //val textureEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D)
        val blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND)
        EventManager.call(RenderEvent())
        //EventHook.setEnabled(GL11.GL_TEXTURE_2D, textureEnabled)
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
        EventManager.call(
            MouseEvent(
                Util.getButton(buttonCode),
                pressed,
                x,
                y,
                0.0
            )
        )

        pressedButton = Util.getButton(buttonCode)
        this.pressed = pressed
    }

    @JvmStatic
    @Suppress("Unused")
    fun onMouseMove() {
        val x = AdapterManager.adapter.mouseLocation[0]
        val y = AdapterManager.adapter.mouseLocation[1]
        EventManager.call(
            MouseEvent(
                Button.NONE,
                false,
                x,
                y,
                0.0
            )
        )
    }

    @JvmStatic
    @Suppress("Unused")
    fun onMouseScroll(amount: Double) {
        val x = AdapterManager.adapter.mouseLocation[0]
        val y = AdapterManager.adapter.mouseLocation[1]
        EventManager.call(
            MouseEvent(
                Button.UNKNOWN,
                false,
                x,
                y,
                amount
            )
        )
    }

    @JvmStatic
    @Suppress("Unused")
    fun onTick() {
        val event = TickEvent()
        EventManager.call(event)
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
        //println(program)
        lastBoundProgram = program
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGetFOV(fov: Double): Double {
        val event = GetFOVEvent(fov)
        EventManager.call(event)
        return event.fov
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGetSensitivity(sensitivity: Double): Double {
        val event = GetSensitivityEvent(sensitivity)
        EventManager.call(event)
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
    fun onCustomPayload(packet: CustomPayloadS2CPacket) {
        if (packet.channel.namespace.equals("sorus")) {
            EventManager.call(SorusCustomPacketEvent(packet.channel.path, packet.data.readString(32767)))
        }
    }

    @JvmStatic
    @Suppress("Unused")
    fun onGameJoin() {
        EventManager.call(GameJoinEvent())
    }

    @JvmStatic
    @Suppress("Unused")
    fun onDisconnect() {
        EventManager.call(GameLeaveEvent())
    }

}