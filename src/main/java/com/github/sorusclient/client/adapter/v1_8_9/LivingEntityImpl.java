package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IItem;
import com.github.sorusclient.client.adapter.ILivingEntity;
import com.github.sorusclient.client.adapter.IPotionEffect;
import v1_8_9.net.minecraft.client.MinecraftClient;
import v1_8_9.net.minecraft.entity.Entity;
import v1_8_9.net.minecraft.entity.LivingEntity;
import v1_8_9.net.minecraft.entity.effect.StatusEffectInstance;
import v1_8_9.net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LivingEntityImpl extends EntityImpl implements ILivingEntity {

    public LivingEntityImpl(Entity entity) {
        super(entity);
    }

    @Override
    public List<IPotionEffect> getEffects() {
        List<IPotionEffect> effects = new ArrayList<>();
        for (StatusEffectInstance effect : ((LivingEntity) this.entity).method_6120()) {
            effects.add(new PotionEffectImpl(effect));
        }
        return effects;
    }

    @Override
    public List<IItem> getArmor() {
        List<IItem> armor = new ArrayList<>();
        for (ItemStack itemStack : this.entity.getArmorStacks()) {
            if (itemStack != null) {
                armor.add(0, new ItemImpl(itemStack));
            }
        }
        return armor;
    }

    @Override
    public double getHealth() {
        return ((LivingEntity) this.entity).getHealth();
    }

    @Override
    public double getMaxHealth() {
        return ((LivingEntity) this.entity).getMaxHealth();
    }

    @Override
    public double getAbsorption() {
        return ((LivingEntity) this.entity).getAbsorption();
    }

}
