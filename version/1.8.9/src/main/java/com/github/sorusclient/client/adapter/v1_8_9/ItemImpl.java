/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IItem;
import com.github.sorusclient.client.adapter.ItemType;
import v1_8_9.net.minecraft.item.Item;
import v1_8_9.net.minecraft.item.ItemStack;

public class ItemImpl implements IItem {

    protected ItemStack itemStack;

    public ItemImpl(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public double getRemainingDurability() {
        return itemStack.getMaxDamage() - itemStack.getDamage();
    }

    @Override
    public double getMaxDurability() {
        return itemStack.getMaxDamage();
    }

    @Override
    public ItemType getType() {
        return switch (Item.getRawId(itemStack.getItem())) {
            case 298 -> ItemType.LEATHER_HELMET;
            case 299 -> ItemType.LEATHER_CHESTPLATE;
            case 300 -> ItemType.LEATHER_LEGGINGS;
            case 301 -> ItemType.LEATHER_BOOTS;
            case 302 -> ItemType.CHAIN_HELMET;
            case 303 -> ItemType.CHAIN_CHESTPLATE;
            case 304 -> ItemType.CHAIN_LEGGINGS;
            case 305 -> ItemType.CHAIN_BOOTS;
            case 306 -> ItemType.IRON_HELMET;
            case 307 -> ItemType.IRON_CHESTPLATE;
            case 308 -> ItemType.IRON_LEGGINGS;
            case 309 -> ItemType.IRON_BOOTS;
            case 310 -> ItemType.DIAMOND_HELMET;
            case 311 -> ItemType.DIAMOND_CHESTPLATE;
            case 312 -> ItemType.DIAMOND_LEGGINGS;
            case 313 -> ItemType.DIAMOND_BOOTS;
            case 314 -> ItemType.GOLD_HELMET;
            case 315 -> ItemType.GOLD_CHESTPLATE;
            case 316 -> ItemType.GOLD_LEGGINGS;
            case 317 -> ItemType.GOLD_BOOTS;
            default -> ItemType.UNKNOWN;
        };
    }

    @Override
    public Object getInner() {
        return itemStack;
    }

    @Override
    public int getCount() {
        return itemStack.count;
    }

}
