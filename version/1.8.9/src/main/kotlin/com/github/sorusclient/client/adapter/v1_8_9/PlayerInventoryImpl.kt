/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9

import com.github.sorusclient.client.adapter.IItem
import com.github.sorusclient.client.adapter.IPlayerInventory
import v1_8_9.net.minecraft.entity.player.PlayerInventory

class PlayerInventoryImpl(private val playerInventory: PlayerInventory) : IPlayerInventory {
    override val selectedSlot: IPlayerInventory.Slot
        get() = IPlayerInventory.Slot.values()[playerInventory.selectedSlot]

    override fun getItem(slot: IPlayerInventory.Slot): IItem? {
        val item = playerInventory.main[slot.ordinal]
        return item?.let { com.github.sorusclient.client.adapter.v1_8_9.ItemImpl(it) }
    }
}