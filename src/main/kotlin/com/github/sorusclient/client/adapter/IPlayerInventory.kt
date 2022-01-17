package com.github.sorusclient.client.adapter

interface IPlayerInventory {
    val selectedSlot: Slot
    fun getItem(slot: Slot): IItem?
    enum class Slot {
        ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE
    }
}