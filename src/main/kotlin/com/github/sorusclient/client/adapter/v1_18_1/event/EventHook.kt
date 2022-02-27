package com.github.sorusclient.client.adapter.v1_18_1.event

import com.github.sorusclient.client.adapter.event.*
import com.github.sorusclient.client.event.EventManager
import org.lwjgl.opengl.GL11C
import v1_18_1.com.mojang.blaze3d.systems.RenderSystem

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

}