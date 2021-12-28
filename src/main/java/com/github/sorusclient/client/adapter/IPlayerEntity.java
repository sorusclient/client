package com.github.sorusclient.client.adapter;

public interface IPlayerEntity extends ILivingEntity {
    double getHunger();
    double getArmorProtection();
    int getExperienceLevel();
    double getExperiencePercentUntilNextLevel();
    IPlayerInventory getInventory();
}
