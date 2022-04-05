/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.adapter.ILivingEntity
import com.github.sorusclient.client.adapter.IPotionEffect
import v1_8_9.net.minecraft.entity.Entity
import v1_8_9.net.minecraft.entity.LivingEntity

open class LivingEntityImpl(entity: Entity) : com.github.sorusclient.client.adapter.v1_8_9.EntityImpl(entity), ILivingEntity {
    override val effects: List<IPotionEffect>
        get() {
            val effects: MutableList<IPotionEffect> = ArrayList()
            for (effect in (entity as LivingEntity).statusEffectInstances) {
                effects.add(com.github.sorusclient.client.adapter.v1_8_9.PotionEffectImpl(effect))
            }
            return effects
        }
    override val armor: List<IItem?>
        get() {
            val armor: MutableList<IItem?> = ArrayList()
            for (itemStack in entity.armorStacks) {
                if (itemStack != null) {
                    armor.add(0, com.github.sorusclient.client.adapter.v1_8_9.ItemImpl(itemStack))
                } else {
                    armor.add(0, null)
                }
            }
            return armor
        }
    override val health: Double
        get() = (entity as LivingEntity).health.toDouble()
    override val maxHealth: Double
        get() = (entity as LivingEntity).maxHealth.toDouble()
    override val absorption: Double
        get() = (entity as LivingEntity).absorption.toDouble()
}