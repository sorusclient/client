/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter;

import java.util.List;

public interface ILivingEntity extends IEntity {
    List<IPotionEffect> getEffects();
    List<IItem> getArmor();
    double getHealth();
    double getMaxHealth();
    double getAbsorption();
}
