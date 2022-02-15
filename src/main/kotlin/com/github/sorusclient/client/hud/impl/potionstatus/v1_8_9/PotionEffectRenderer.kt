package com.github.sorusclient.client.hud.impl.potionstatus.v1_8_9

import com.github.glassmc.loader.api.GlassLoader
import com.github.glassmc.loader.api.Listener
import com.github.sorusclient.client.adapter.IPotionEffect.PotionType
import com.github.sorusclient.client.hud.impl.potionstatus.IPotionEffectRenderer
import org.lwjgl.opengl.GL11
import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.client.gui.DrawableHelper
import v1_8_9.net.minecraft.entity.effect.StatusEffect
import v1_8_9.net.minecraft.util.Identifier

class PotionEffectRenderer : Listener, IPotionEffectRenderer {
    override fun run() {
        GlassLoader.getInstance().registerInterface(IPotionEffectRenderer::class.java, this)
    }

    override fun render(type: PotionType?, x: Double, y: Double, scale: Double) {
        val id: Int
        id = when (type) {
            PotionType.SPEED -> 1
            PotionType.SLOWNESS -> 2
            PotionType.HASTE -> 3
            PotionType.MINING_FATIGUE -> 4
            PotionType.STRENGTH -> 5
            PotionType.INSTANT_HEALTH -> 6
            PotionType.INSTANT_DAMAGE -> 7
            PotionType.JUMP_BOOST -> 8
            PotionType.NAUSEA -> 9
            PotionType.REGENERATION -> 10
            PotionType.RESISTANCE -> 11
            PotionType.FIRE_RESISTANCE -> 12
            PotionType.WATER_BREATHING -> 13
            PotionType.INVISIBILITY -> 14
            PotionType.BLINDNESS -> 15
            PotionType.NIGHT_VISION -> 16
            PotionType.HUNGER -> 17
            PotionType.WEAKNESS -> 18
            PotionType.POISON -> 19
            PotionType.WITHER -> 20
            PotionType.HEALTH_BOOST -> 21
            PotionType.ABSORPTION -> 22
            PotionType.SATURATION -> 22
            else -> -1
        }
        val index = StatusEffect.STATUS_EFFECTS[id].method_2444()
        val textureEnabled = GL11.glIsEnabled(GL11.GL_TEXTURE_2D)
        val blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND)
        MinecraftClient.getInstance().textureManager.bindTexture(Identifier("textures/gui/container/inventory.png"))
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
        GL11.glPushMatrix()
        GL11.glTranslated(x, y, 0.0)
        GL11.glScaled(scale, scale, 0.0)
        DrawableHelper().drawTexture(0, 0, index % 8 * 18, 198 + index / 8 * 18, 18, 18)
        GL11.glPopMatrix()
        if (textureEnabled) {
            GL11.glEnable(GL11.GL_TEXTURE_2D)
        } else {
            GL11.glDisable(GL11.GL_TEXTURE_2D)
        }
        if (blendEnabled) {
            GL11.glEnable(GL11.GL_BLEND)
        } else {
            GL11.glDisable(GL11.GL_BLEND)
        }
    }
}