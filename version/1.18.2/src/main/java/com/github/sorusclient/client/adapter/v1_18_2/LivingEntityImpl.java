/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2;

import com.github.sorusclient.client.adapter.IItem;
import com.github.sorusclient.client.adapter.ILivingEntity;
import com.github.sorusclient.client.adapter.IPotionEffect;
import v1_18_2.net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class LivingEntityImpl<T extends LivingEntity> extends EntityImpl<T> implements ILivingEntity {

    public LivingEntityImpl(T entity) {
        super(entity);
    }

    @Override
    public List<IPotionEffect> getEffects() {
        List<IPotionEffect> effects = new ArrayList<>();
        for (var effect : entity.getStatusEffects()) {
            effects.add(new PotionEffectImpl(effect));
        }
        return effects;
    }

    @Override
    public List<IItem> getArmor() {
        List<IItem> armors = new ArrayList<>();
        for (var armor : entity.getArmorItems()) {
            if (armor != null) {
                armors.add(0, new ItemImpl(armor));
            } else {
                armors.add(0, null);
            }
        }
        return armors;
    }

    @Override
    public double getHealth() {
        return entity.getHealth();
    }

    @Override
    public double getMaxHealth() {
        return entity.getMaxHealth();
    }

    @Override
    public double getAbsorption() {
        return entity.getAbsorptionAmount();
    }

}
