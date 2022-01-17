package com.github.sorusclient.client.adapter

interface ILivingEntity : IEntity {
    val effects: List<IPotionEffect>
    val armor: List<IItem?>
    val health: Double
    val maxHealth: Double
    val absorption: Double
}