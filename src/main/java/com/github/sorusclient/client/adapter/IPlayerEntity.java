/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter;

public interface IPlayerEntity extends ILivingEntity {
    double getHunger();
    double getArmorProtection();
    int getExperienceLevel();
    double getExperiencePercent();
    IPlayerInventory getInventory();
}
