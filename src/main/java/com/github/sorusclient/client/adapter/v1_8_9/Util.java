package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.Button;
import com.github.sorusclient.client.adapter.IItem;
import com.github.sorusclient.client.adapter.Key;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

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

    private static final BiMap<Integer, Key> keyMap = HashBiMap.create();

    static {
        keyMap.put(1, Key.ESCAPE);
        keyMap.put(16, Key.Q);
        keyMap.put(17, Key.W);
        keyMap.put(18, Key.E);
        keyMap.put(19, Key.R);
        keyMap.put(20, Key.T);
        keyMap.put(21, Key.Y);
        keyMap.put(22, Key.U);
        keyMap.put(23, Key.I);
        keyMap.put(24, Key.O);
        keyMap.put(25, Key.P);
        keyMap.put(29, Key.CONTROL_LEFT);
        keyMap.put(30, Key.A);
        keyMap.put(31, Key.S);
        keyMap.put(32, Key.D);
        keyMap.put(33, Key.F);
        keyMap.put(34, Key.G);
        keyMap.put(35, Key.H);
        keyMap.put(36, Key.J);
        keyMap.put(37, Key.K);
        keyMap.put(38, Key.L);
        keyMap.put(42, Key.SHIFT_LEFT);
        keyMap.put(44, Key.Z);
        keyMap.put(45, Key.X);
        keyMap.put(46, Key.C);
        keyMap.put(47, Key.V);
        keyMap.put(48, Key.B);
        keyMap.put(49, Key.N);
        keyMap.put(50, Key.M);
    }

    public static Key getKey(int id) {
        return keyMap.getOrDefault(id, Key.UNKNOWN);
    }

    private static final BiMap<Integer, Button> buttonMap = HashBiMap.create();

    static {
        buttonMap.put(0, Button.PRIMARY);
        buttonMap.put(-1, Button.NONE);
    }

    public static Button getButton(int id) {
        return buttonMap.getOrDefault(id, Button.UNKNOWN);
    }
    
}
