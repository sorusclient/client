package com.github.sorusclient.client.adapter.v1_18_2

import com.github.sorusclient.client.adapter.IPotionEffect
import com.github.sorusclient.client.adapter.IPotionEffect.PotionType
import v1_18_2.net.minecraft.client.resource.language.I18n
import v1_18_2.net.minecraft.entity.effect.StatusEffect
import v1_18_2.net.minecraft.entity.effect.StatusEffectInstance

class PotionEffectImpl(protected val effect: StatusEffectInstance) : IPotionEffect {

    override val duration: String
        get() {
            if (effect.isPermanent) {
                return "**:**"
            }
            val duration = effect.duration
            val seconds = duration / 20
            val minutes = seconds / 60
            val secondsReal = if (minutes == 0) seconds else seconds % (minutes * 60)
            return minutes.toString() + ":" + (if (secondsReal < 10) "0" else "") + secondsReal
        }

    override val name: String?
        get() = I18n.translate(effect.translationKey)

    override val amplifier: Int
        get() = effect.amplifier + 1

    override val type: PotionType
        get() = when (StatusEffect.getRawId(effect.effectType)) {
            1 -> PotionType.SPEED
            2 -> PotionType.SLOWNESS
            3 -> PotionType.HASTE
            4 -> PotionType.MINING_FATIGUE
            5 -> PotionType.STRENGTH
            6 -> PotionType.INSTANT_HEALTH
            7 -> PotionType.INSTANT_DAMAGE
            8 -> PotionType.JUMP_BOOST
            9 -> PotionType.NAUSEA
            10 -> PotionType.REGENERATION
            11 -> PotionType.RESISTANCE
            12 -> PotionType.FIRE_RESISTANCE
            13 -> PotionType.WATER_BREATHING
            14 -> PotionType.INVISIBILITY
            15 -> PotionType.BLINDNESS
            16 -> PotionType.NIGHT_VISION
            17 -> PotionType.HUNGER
            18 -> PotionType.WEAKNESS
            19 -> PotionType.POISON
            20 -> PotionType.WITHER
            21 -> PotionType.HEALTH_BOOST
            22 -> PotionType.ABSORPTION
            23 -> PotionType.SATURATION
            24 -> PotionType.GLOWING
            25 -> PotionType.LEVITATION
            26 -> PotionType.LUCK
            27 -> PotionType.UNLUCK
            28 -> PotionType.SLOW_FALLING
            29 -> PotionType.CONDUIT_POWER
            30 -> PotionType.DOLPHINS_GRACE
            31 -> PotionType.BAD_OMEN
            32 -> PotionType.HERO_OF_THE_VILLAGE
            else -> PotionType.UNKNOWN
        }

}