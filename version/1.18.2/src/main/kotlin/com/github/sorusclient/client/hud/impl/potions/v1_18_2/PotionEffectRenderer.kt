/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.hud.impl.potions.v1_18_2

import com.github.sorusclient.client.InterfaceManager
import com.github.sorusclient.client.adapter.IPotionEffect.PotionType
import com.github.sorusclient.client.bootstrap.Initializer
import com.github.sorusclient.client.hud.impl.potions.IPotionEffectRenderer
import v1_18_2.com.mojang.blaze3d.systems.RenderSystem
import v1_18_2.net.minecraft.client.MinecraftClient
import v1_18_2.net.minecraft.client.render.*
import v1_18_2.net.minecraft.entity.effect.StatusEffects

class PotionEffectRenderer : IPotionEffectRenderer, Initializer {

    override fun initialize() {
        InterfaceManager.register(this)
    }

    override fun render(type: PotionType?, x: Double, y: Double, scale: Double) {
        val potion = when (type) {
            PotionType.SPEED -> StatusEffects.SPEED
            PotionType.SLOWNESS -> StatusEffects.SLOWNESS
            PotionType.HASTE -> StatusEffects.HASTE
            PotionType.MINING_FATIGUE -> StatusEffects.MINING_FATIGUE
            PotionType.STRENGTH -> StatusEffects.STRENGTH
            PotionType.INSTANT_HEALTH -> StatusEffects.INSTANT_HEALTH
            PotionType.INSTANT_DAMAGE -> StatusEffects.INSTANT_DAMAGE
            PotionType.JUMP_BOOST -> StatusEffects.JUMP_BOOST
            PotionType.NAUSEA -> StatusEffects.NAUSEA
            PotionType.REGENERATION -> StatusEffects.REGENERATION
            PotionType.RESISTANCE -> StatusEffects.RESISTANCE
            PotionType.FIRE_RESISTANCE -> StatusEffects.FIRE_RESISTANCE
            PotionType.WATER_BREATHING -> StatusEffects.WATER_BREATHING
            PotionType.INVISIBILITY -> StatusEffects.INVISIBILITY
            PotionType.BLINDNESS -> StatusEffects.BLINDNESS
            PotionType.NIGHT_VISION -> StatusEffects.NIGHT_VISION
            PotionType.HUNGER -> StatusEffects.HUNGER
            PotionType.WEAKNESS -> StatusEffects.WEAKNESS
            PotionType.POISON -> StatusEffects.POISON
            PotionType.WITHER -> StatusEffects.WITHER
            PotionType.HEALTH_BOOST -> StatusEffects.HEALTH_BOOST
            PotionType.ABSORPTION -> StatusEffects.ABSORPTION
            PotionType.SATURATION -> StatusEffects.SATURATION
            PotionType.GLOWING -> StatusEffects.GLOWING
            PotionType.LEVITATION -> StatusEffects.LEVITATION
            PotionType.LUCK -> StatusEffects.LUCK
            PotionType.UNLUCK -> StatusEffects.UNLUCK
            PotionType.SLOW_FALLING -> StatusEffects.SLOW_FALLING
            PotionType.CONDUIT_POWER -> StatusEffects.CONDUIT_POWER
            PotionType.DOLPHINS_GRACE -> StatusEffects.DOLPHINS_GRACE
            PotionType.BAD_OMEN -> StatusEffects.BAD_OMEN
            PotionType.HERO_OF_THE_VILLAGE -> StatusEffects.HERO_OF_THE_VILLAGE
            else -> null!!
        }

        val statusEffectSpriteManager = MinecraftClient.getInstance().statusEffectSpriteManager
        val sprite = statusEffectSpriteManager.getSprite(potion)

        RenderSystem.enableBlend()
        RenderSystem.enableTexture()
        RenderSystem.setShaderTexture(0, sprite.atlas.id)

        drawTexture(x, y, (sprite.minU.toDouble() * 256), (sprite.minV.toDouble() * 256), 18 * scale, 18 * scale, ((sprite.maxU - sprite.minU).toDouble() * 256).toInt(), ((sprite.maxV - sprite.minV).toDouble() * 256).toInt())
    }

    private fun drawTexture(var1: Double, var2: Double, textureX: Double, textureY: Double, width: Double, height: Double, textureWidth: Int, textureHeight: Int) {
        val textureX = textureX + 0.1
        val width = width - 0.1

        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)

        val var9 = Tessellator.getInstance()
        val var10 = var9.buffer
        var10.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE)
        var10.vertex((var1 + 0), (var2 + height), 0.0).texture((textureX + 0).toFloat() / 256, ((textureY + textureHeight).toFloat() / 256)).next()
        var10.vertex((var1 + width), (var2 + height), 0.0).texture(((textureX + textureWidth).toFloat() / 256), ((textureY + textureHeight).toFloat() / 256)).next()
        var10.vertex((var1 + width), (var2 + 0), 0.0).texture(((textureX + textureWidth).toFloat() / 256), ((textureY + 0).toFloat() / 256)).next()
        var10.vertex((var1 + 0), (var2 + 0), 0.0).texture(((textureX + 0).toFloat() / 256), ((textureY + 0).toFloat() / 256)).next()
        var10.end()
        BufferRenderer.draw(var10)
    }

}