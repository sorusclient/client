package com.github.sorusclient.client.feature.impl.oldanimations.v1_8_9

import v1_8_9.net.minecraft.client.MinecraftClient
import v1_8_9.net.minecraft.entity.effect.StatusEffect
import v1_8_9.net.minecraft.util.hit.HitResult

@Suppress("UNUSED")
object OldAnimationsHook {

    @JvmStatic
    fun getPartialTicks(partialTicks: Float): Float {
        return if (OldAnimations.isOldBlockHitValue()) {
            partialTicks
        } else {
            0.0F
        }
    }

    @JvmStatic
    //https://github.com/sp614x/optifine/issues/2098
    fun updateSwing() {
        val mc = MinecraftClient.getInstance()
        val player = mc.player ?: return
        if (player.mainHandStack != null && OldAnimations.isOldBlockHitValue()) {
            if (player.itemUseTicks > 0) {
                val mouseDown = mc.options.keyAttack.isPressed && mc.options.keyUse.isPressed
                if (mouseDown && mc.result != null && mc.result.type == HitResult.Type.BLOCK) {
                    val swingAnimationEnd =
                        if (player.hasStatusEffect(StatusEffect.HASTE)) 6 - (1 + player.getEffectInstance(StatusEffect.HASTE).amplifier) else if (player.hasStatusEffect(
                                StatusEffect.MINING_FATIGUE
                            )
                        ) 6 + (1 + player.getEffectInstance(StatusEffect.MINING_FATIGUE).amplifier) * 2 else 6
                    if (!player.handSwinging || player.handSwingTicks >= swingAnimationEnd / 2 || player.handSwingTicks < 0) {
                        player.handSwingTicks = -1
                        player.handSwinging = true
                    }
                }
            }
        }
    }

    @JvmStatic
    fun showArmorDamage(): Boolean {
        return com.github.sorusclient.client.feature.impl.oldanimations.OldAnimations.showArmorDamageValue()
    }

}