package com.github.sorusclient.client.adapter.v1_8_9;

import com.github.sorusclient.client.adapter.IItem;
import com.github.sorusclient.client.adapter.IPlayerInventory;
import v1_8_9.net.minecraft.entity.player.PlayerInventory;
import v1_8_9.net.minecraft.item.ItemStack;

public class PlayerInventoryImpl implements IPlayerInventory {

    private final PlayerInventory playerInventory;

    public PlayerInventoryImpl(PlayerInventory playerInventory) {
        this.playerInventory = playerInventory;
    }

    @Override
    public Slot getSelectedSlot() {
        return Slot.values()[this.playerInventory.selectedSlot];
    }

    @Override
    public IItem getItem(Slot slot) {
        ItemStack item = this.playerInventory.main[slot.ordinal()];
        return item != null ? new ItemImpl(item) : null;
    }

}
