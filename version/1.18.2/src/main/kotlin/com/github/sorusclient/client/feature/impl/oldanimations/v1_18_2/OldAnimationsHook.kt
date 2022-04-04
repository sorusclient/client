package com.github.sorusclient.client.feature.impl.oldanimations.v1_18_2

import com.github.sorusclient.client.feature.impl.oldanimations.OldAnimations
import v1_18_2.net.minecraft.entity.LivingEntity

@Suppress("UNUSED")
object OldAnimationsHook {

    private var livingEntity: LivingEntity? = null

    @JvmStatic
    fun init(livingEntity: LivingEntity) {
        this.livingEntity = livingEntity
    }

    @JvmStatic
    fun modifyRedColor(color: Float): Float {
        return if (showArmorDamage() && livingEntity!!.hurtTime > 0) {
            color * 1f
        } else {
            color
        }
    }

    @JvmStatic
    fun modifyGreenColor(color: Float): Float {
        return if (showArmorDamage() && livingEntity!!.hurtTime > 0) {
            color * 0.6f
        } else {
            color
        }
    }

    @JvmStatic
    fun modifyBlueColor(color: Float): Float {
        return if (showArmorDamage() && livingEntity!!.hurtTime > 0) {
            color * 0.6f
        } else {
            color
        }
    }

    @JvmStatic
    fun showArmorDamage(): Boolean {
        return OldAnimations.showArmorDamageValue()
    }

}