/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_18_2

import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.adapter.IItem.ItemType
import v1_18_2.net.minecraft.item.Item
import v1_18_2.net.minecraft.item.ItemStack

class ItemImpl(private val itemStack: ItemStack) : IItem {

    override val remainingDurability: Double
        get() = maxDurability - itemStack.damage

    override val maxDurability: Double
        get() = itemStack.maxDamage.toDouble()

    override val type: ItemType
        get() = when (Item.getRawId(itemStack.item)) {
            298 -> ItemType.LEATHER_HELMET
            299 -> ItemType.LEATHER_CHESTPLATE
            300 -> ItemType.LEATHER_LEGGINGS
            301 -> ItemType.LEATHER_BOOTS
            302 -> ItemType.CHAIN_HELMET
            303 -> ItemType.CHAIN_CHESTPLATE
            304 -> ItemType.CHAIN_LEGGINGS
            305 -> ItemType.CHAIN_BOOTS
            306 -> ItemType.IRON_HELMET
            307 -> ItemType.IRON_CHESTPLATE
            308 -> ItemType.IRON_LEGGINGS
            309 -> ItemType.IRON_BOOTS
            310 -> ItemType.DIAMOND_HELMET
            311 -> ItemType.DIAMOND_CHESTPLATE
            312 -> ItemType.DIAMOND_LEGGINGS
            313 -> ItemType.DIAMOND_BOOTS
            314 -> ItemType.GOLD_HELMET
            315 -> ItemType.GOLD_CHESTPLATE
            316 -> ItemType.GOLD_LEGGINGS
            317 -> ItemType.GOLD_BOOTS
            else -> ItemType.UNKNOWN
        }

    override val inner: Any
        get() = itemStack

    override val count: Int
        get() = itemStack.count
}