package com.github.sorusclient.client.adapter.v1_18_1

import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.adapter.IPlayerInventory
import v1_18_1.net.minecraft.entity.player.PlayerInventory

class PlayerInventoryImpl(private val playerInventory: PlayerInventory) : IPlayerInventory {

    override val selectedSlot: IPlayerInventory.Slot
        get() = IPlayerInventory.Slot.values()[playerInventory.selectedSlot]

    override fun getItem(slot: IPlayerInventory.Slot): IItem {
        val item = playerInventory.main[slot.ordinal]
        return ItemImpl(item)
    }

}