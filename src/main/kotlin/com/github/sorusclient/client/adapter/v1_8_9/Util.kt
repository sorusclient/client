package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.Button
import com.github.sorusclient.client.adapter.IItem.ItemType
import com.github.sorusclient.client.adapter.Key
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap

object Util {
    fun getIdByItemType(itemType: ItemType?): Int {
        return when (itemType) {
            ItemType.LEATHER_HELMET -> 298
            ItemType.LEATHER_CHESTPLATE -> 299
            ItemType.LEATHER_LEGGINGS -> 300
            ItemType.LEATHER_BOOTS -> 301
            ItemType.CHAIN_HELMET -> 302
            ItemType.CHAIN_CHESTPLATE -> 303
            ItemType.CHAIN_LEGGINGS -> 304
            ItemType.CHAIN_BOOTS -> 305
            ItemType.IRON_HELMET -> 306
            ItemType.IRON_CHESTPLATE -> 307
            ItemType.IRON_LEGGINGS -> 308
            ItemType.IRON_BOOTS -> 309
            ItemType.DIAMOND_HELMET -> 310
            ItemType.DIAMOND_CHESTPLATE -> 311
            ItemType.DIAMOND_LEGGINGS -> 312
            ItemType.DIAMOND_BOOTS -> 313
            ItemType.GOLD_HELMET -> 314
            ItemType.GOLD_CHESTPLATE -> 315
            ItemType.GOLD_LEGGINGS -> 316
            ItemType.GOLD_BOOTS -> 317
            else -> -1
        }
    }

    private val keyMap: BiMap<Int, Key> = HashBiMap.create()

    init {
        keyMap[1] = Key.ESCAPE
        keyMap[16] = Key.Q
        keyMap[17] = Key.W
        keyMap[18] = Key.E
        keyMap[19] = Key.R
        keyMap[20] = Key.T
        keyMap[21] = Key.Y
        keyMap[22] = Key.U
        keyMap[23] = Key.I
        keyMap[24] = Key.O
        keyMap[25] = Key.P
        keyMap[29] = Key.CONTROL_LEFT
        keyMap[30] = Key.A
        keyMap[31] = Key.S
        keyMap[32] = Key.D
        keyMap[33] = Key.F
        keyMap[34] = Key.G
        keyMap[35] = Key.H
        keyMap[36] = Key.J
        keyMap[37] = Key.K
        keyMap[38] = Key.L
        keyMap[42] = Key.SHIFT_LEFT
        keyMap[44] = Key.Z
        keyMap[45] = Key.X
        keyMap[46] = Key.C
        keyMap[47] = Key.V
        keyMap[48] = Key.B
        keyMap[49] = Key.N
        keyMap[50] = Key.M
    }

    fun getKey(id: Int): Key {
        return keyMap.getOrDefault(id, Key.UNKNOWN)
    }

    private val buttonMap: BiMap<Int, Button> = HashBiMap.create()

    init {
        buttonMap[0] = Button.PRIMARY
        buttonMap[-1] = Button.NONE
    }

    fun getButton(id: Int): Button {
        return buttonMap.getOrDefault(id, Button.UNKNOWN)
    }
}