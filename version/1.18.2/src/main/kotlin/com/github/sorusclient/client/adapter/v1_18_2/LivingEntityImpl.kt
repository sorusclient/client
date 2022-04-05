/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2

import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.adapter.ILivingEntity
import com.github.sorusclient.client.adapter.IPotionEffect
import v1_18_2.net.minecraft.entity.Entity
import v1_18_2.net.minecraft.entity.LivingEntity
import v1_18_2.net.minecraft.item.Items

open class LivingEntityImpl(entity: Entity) : EntityImpl(entity), ILivingEntity {

    override val effects: List<IPotionEffect>
        get() {
            val effects: MutableList<IPotionEffect> = ArrayList()
            for (effect in (entity as LivingEntity).statusEffects) {
                effects.add(PotionEffectImpl(effect))
            }
            return effects
        }

    override val armor: List<IItem?>
        get() {
            val armor: MutableList<IItem?> = ArrayList()
            for (itemStack in entity.armorItems) {
                if (itemStack != null && itemStack.item != Items.AIR) {
                    armor.add(0, ItemImpl(itemStack))
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
        get() = (entity as LivingEntity).absorptionAmount.toDouble()

}