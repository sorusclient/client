/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter

interface IPlayerEntity : ILivingEntity {
    val hunger: Double
    val armorProtection: Double
    val experienceLevel: Int
    val experiencePercentUntilNextLevel: Double
    val inventory: IPlayerInventory
}