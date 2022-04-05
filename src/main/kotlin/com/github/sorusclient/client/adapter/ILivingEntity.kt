/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter

interface ILivingEntity : IEntity {
    val effects: List<IPotionEffect>
    val armor: List<IItem?>
    val health: Double
    val maxHealth: Double
    val absorption: Double
}