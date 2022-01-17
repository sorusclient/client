package com.github.sorusclient.client.adapter

interface IPlayerEntity : ILivingEntity {
    val hunger: Double
    val armorProtection: Double
    val experienceLevel: Int
    val experiencePercentUntilNextLevel: Double
    val inventory: IPlayerInventory
}