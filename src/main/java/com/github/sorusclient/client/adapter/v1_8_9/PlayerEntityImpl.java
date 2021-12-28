package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IPlayerInventory;
import com.github.sorusclient.client.adapter.IPlayerEntity;
import v1_8_9.net.minecraft.entity.Entity;
import v1_8_9.net.minecraft.entity.player.PlayerEntity;

public class PlayerEntityImpl extends LivingEntityImpl implements IPlayerEntity {

    public PlayerEntityImpl(Entity entity) {
        super(entity);
    }

    @Override
    public double getHunger() {
        return ((PlayerEntity) this.entity).getHungerManager().getFoodLevel();
    }

    @Override
    public double getArmorProtection() {
        return ((PlayerEntity) this.entity).getArmorProtectionValue();
    }

    @Override
    public int getExperienceLevel() {
        return ((PlayerEntity) this.entity).experienceLevel;
    }

    @Override
    public double getExperiencePercentUntilNextLevel() {
        return ((PlayerEntity) this.entity).experienceProgress;
    }

    @Override
    public IPlayerInventory getInventory() {
        return new PlayerInventoryImpl(((PlayerEntity) this.entity).inventory);
    }

}
