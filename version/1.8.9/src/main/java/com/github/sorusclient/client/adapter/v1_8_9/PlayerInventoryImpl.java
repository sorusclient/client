/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IItem;
import com.github.sorusclient.client.adapter.IPlayerInventory;
import com.github.sorusclient.client.adapter.InventorySlot;
import v1_8_9.net.minecraft.entity.player.PlayerInventory;

public class PlayerInventoryImpl implements IPlayerInventory {

    private final PlayerInventory inventory;

    public PlayerInventoryImpl(PlayerInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public InventorySlot getSelectedSlot() {
        return InventorySlot.values()[inventory.selectedSlot];
    }

    @Override
    public IItem getItem(InventorySlot slot) {
        return null;
    }

}
