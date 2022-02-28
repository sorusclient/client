package com.github.sorusclient.client.adapter.v1_18_1.event

import com.github.sorusclient.client.adapter.AdapterManager
import com.github.sorusclient.client.adapter.Button
import com.github.sorusclient.client.adapter.event.*
import com.github.sorusclient.client.adapter.v1_18_1.Util
import com.github.sorusclient.client.adapter.v1_8_9.event.EventHook
import com.github.sorusclient.client.event.EventManager
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11C
import v1_18_1.com.mojang.blaze3d.systems.RenderSystem
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.util.Window

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
        val x = AdapterManager.getAdapter().mouseLocation[0]
        val y = AdapterManager.getAdapter().mouseLocation[1]
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
        val x = AdapterManager.getAdapter().mouseLocation[0]
        val y = AdapterManager.getAdapter().mouseLocation[1]
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
        val x = AdapterManager.getAdapter().mouseLocation[0]
        val y = AdapterManager.getAdapter().mouseLocation[1]
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

}