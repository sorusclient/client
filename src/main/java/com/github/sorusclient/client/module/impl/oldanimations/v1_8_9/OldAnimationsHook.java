package com.github.sorusclient.client.module.impl.oldanimations.v1_8_9;

import com.github.sorusclient.client.Sorus;
import com.github.sorusclient.client.module.ModuleManager;
import com.github.sorusclient.client.module.impl.oldanimations.OldAnimations;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.client.network.ClientPlayerEntity;
import v1_8_9.net.minecraft.entity.effect.StatusEffect;
import v1_8_9.net.minecraft.util.hit.HitResult;

public class OldAnimationsHook {

    public static float getPartialTicks(float partialTicks) {
        OldAnimations oldAnimations = Sorus.getInstance().get(ModuleManager.class).get(OldAnimations.class);
        if (oldAnimations.isEnabled() && oldAnimations.isOldBlockHit()) {
            return partialTicks;
        } else {
            return 0;
        }
    }

    //https://github.com/sp614x/optifine/issues/2098
    public static void updateSwing() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            return;
        }

        OldAnimations oldAnimations = Sorus.getInstance().get(ModuleManager.class).get(OldAnimations.class);
        if (player.getMainHandStack() != null && oldAnimations.isEnabled() && oldAnimations.isOldBlockHit()) {
            if (player.method_3192() > 0) {
                final boolean mouseDown = mc.options.keyAttack.isPressed() && mc.options.keyUse.isPressed();
                if (mouseDown && mc.result != null && mc.result.type == HitResult.Type.BLOCK) {
                    final int swingAnimationEnd = player.hasStatusEffect(StatusEffect.HASTE) ? (6 - (1 + player.getEffectInstance(StatusEffect.HASTE).getAmplifier())) : (player.hasStatusEffect(StatusEffect.MINING_FATIGUE) ? (6 + (1 + player.getEffectInstance(StatusEffect.MINING_FATIGUE).getAmplifier()) * 2) : 6);
                    if (!player.handSwinging || player.handSwingTicks >= swingAnimationEnd / 2 || player.handSwingTicks < 0) {
                        player.handSwingTicks = -1;
                        player.handSwinging = true;
                    }
                }
            }
        }
    }

    public static boolean showArmorDamage() {
        OldAnimations oldAnimations = Sorus.getInstance().get(ModuleManager.class).get(OldAnimations.class);
        return oldAnimations.isEnabled() && oldAnimations.showArmorDamage();
    }

}
