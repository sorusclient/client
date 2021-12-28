package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IItem;
import v1_8_9.net.minecraft.item.Item;
import v1_8_9.net.minecraft.item.ItemStack;

public class ItemImpl implements IItem {

    protected final ItemStack itemStack;

    public ItemImpl(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public double getRemainingDurability() {
        return this.getMaxDurability() - this.itemStack.getDamage();
    }

    @Override
    public double getMaxDurability() {
        return this.itemStack.getMaxDamage();
    }

    @Override
    public ItemType getType() {
        switch (Item.getRawId(this.itemStack.getItem())) {
            case 298:
                return ItemType.LEATHER_HELMET;
            case 299:
                return ItemType.LEATHER_CHESTPLATE;
            case 300:
                return ItemType.LEATHER_LEGGINGS;
            case 301:
                return ItemType.LEATHER_BOOTS;
            case 302:
                return ItemType.CHAIN_HELMET;
            case 303:
                return ItemType.CHAIN_CHESTPLATE;
            case 304:
                return ItemType.CHAIN_LEGGINGS;
            case 305:
                return ItemType.CHAIN_BOOTS;
            case 306:
                return ItemType.IRON_HELMET;
            case 307:
                return ItemType.IRON_CHESTPLATE;
            case 308:
                return ItemType.IRON_LEGGINGS;
            case 309:
                return ItemType.IRON_BOOTS;
            case 310:
                return ItemType.DIAMOND_HELMET;
            case 311:
                return ItemType.DIAMOND_CHESTPLATE;
            case 312:
                return ItemType.DIAMOND_LEGGINGS;
            case 313:
                return ItemType.DIAMOND_BOOTS;
            case 314:
                return ItemType.GOLD_HELMET;
            case 315:
                return ItemType.GOLD_CHESTPLATE;
            case 316:
                return ItemType.GOLD_LEGGINGS;
            case 317:
                return ItemType.GOLD_BOOTS;
            default:
                return ItemType.UNKNOWN;
        }
    }

    @Override
    public Object getInner() {
        return this.itemStack;
    }

    @Override
    public int getCount() {
        return this.itemStack.count;
    }

}
