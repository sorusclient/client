/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IPlayerEntity;
import com.github.sorusclient.client.adapter.IPlayerInventory;
import org.jetbrains.annotations.NotNull;
import v1_8_9.net.minecraft.entity.player.PlayerEntity;

public class PlayerEntityImpl extends LivingEntityImpl<PlayerEntity> implements IPlayerEntity {

    public PlayerEntityImpl(PlayerEntity entity) {
        super(entity);
    }

    @Override
    public double getHunger() {
        return entity.getHungerManager().getFoodLevel();
    }

    @Override
    public double getArmorProtection() {
        return entity.getArmorProtectionValue();
    }

    @Override
    public int getExperienceLevel() {
        return entity.experienceLevel;
    }

    @Override
    public double getExperiencePercentUntilNextLevel() {
        return entity.experienceProgress;
    }

    @NotNull
    @Override
    public IPlayerInventory getInventory() {
        return new PlayerInventoryImpl(entity.inventory);
    }
}
