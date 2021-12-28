package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IItem;

public class Util {
    
    public static int getIdByItemType(IItem.ItemType itemType) {
        switch (itemType) {
            case LEATHER_HELMET:
                return 298;
            case LEATHER_CHESTPLATE:
                return 299;
            case LEATHER_LEGGINGS:
                return 300;
            case LEATHER_BOOTS:
                return 301;
            case CHAIN_HELMET:
                return 302;
            case CHAIN_CHESTPLATE:
                return 303;
            case CHAIN_LEGGINGS:
                return 304;
            case CHAIN_BOOTS:
                return 305;
            case IRON_HELMET:
                return 306;
            case IRON_CHESTPLATE:
                return 307;
            case IRON_LEGGINGS:
                return 308;
            case IRON_BOOTS:
                return 309;
            case DIAMOND_HELMET:
                return 310;
            case DIAMOND_CHESTPLATE:
                return 311;
            case DIAMOND_LEGGINGS:
                return 312;
            case DIAMOND_BOOTS:
                return 313;
            case GOLD_HELMET:
                return 314;
            case GOLD_CHESTPLATE:
                return 315;
            case GOLD_LEGGINGS:
                return 316;
            case GOLD_BOOTS:
                return 317;
            default:
                return -1;
                
        }
    }
    
}
