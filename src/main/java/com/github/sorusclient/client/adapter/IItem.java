package com.github.sorusclient.client.adapter;

public interface IItem {

    double getRemainingDurability();
    double getMaxDurability();
    ItemType getType();
    Object getInner();
    int getCount();
     
     enum ItemType {
         LEATHER_HELMET,
         LEATHER_CHESTPLATE,
         LEATHER_LEGGINGS,
         LEATHER_BOOTS,
         GOLD_HELMET,
         GOLD_CHESTPLATE,
         GOLD_LEGGINGS,
         GOLD_BOOTS,
         CHAIN_HELMET,
         CHAIN_CHESTPLATE,
         CHAIN_LEGGINGS,
         CHAIN_BOOTS,
         IRON_HELMET,
         IRON_CHESTPLATE,
         IRON_LEGGINGS,
         IRON_BOOTS,
         DIAMOND_HELMET,
         DIAMOND_CHESTPLATE,
         DIAMOND_LEGGINGS,
         DIAMOND_BOOTS,
         UNKNOWN,
     }

}
