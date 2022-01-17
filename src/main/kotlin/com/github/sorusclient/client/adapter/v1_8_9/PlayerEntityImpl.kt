package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IPlayerEntity
import com.github.sorusclient.client.adapter.IPlayerInventory
import v1_8_9.net.minecraft.entity.Entity
import v1_8_9.net.minecraft.entity.player.PlayerEntity

class PlayerEntityImpl(entity: Entity) : LivingEntityImpl(entity), IPlayerEntity {
    override val hunger: Double
        get() = (entity as PlayerEntity).hungerManager.foodLevel.toDouble()
    override val armorProtection: Double
        get() = (entity as PlayerEntity).armorProtectionValue.toDouble()
    override val experienceLevel: Int
        get() = (entity as PlayerEntity).experienceLevel
    override val experiencePercentUntilNextLevel: Double
        get() = (entity as PlayerEntity).experienceProgress.toDouble()
    override val inventory: IPlayerInventory
        get() = PlayerInventoryImpl((entity as PlayerEntity).inventory)
}