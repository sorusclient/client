/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2

import com.github.sorusclient.client.adapter.IPlayerEntity
import com.github.sorusclient.client.adapter.IPlayerInventory
import v1_18_2.net.minecraft.entity.Entity
import v1_18_2.net.minecraft.entity.player.PlayerEntity
import java.util.*

class PlayerEntityImpl(entity: Entity) : LivingEntityImpl(entity), IPlayerEntity {

    override val hunger: Double
        get() = (entity as PlayerEntity).hungerManager.foodLevel.toDouble()

    override val armorProtection: Double
        get() = (entity as PlayerEntity).armor.toDouble()

    override val experienceLevel: Int
        get() = (entity as PlayerEntity).experienceLevel

    override val experiencePercentUntilNextLevel: Double
        get() = (entity as PlayerEntity).experienceProgress.toDouble()

    override val inventory: IPlayerInventory
        get() = PlayerInventoryImpl((entity as PlayerEntity).inventory)

}