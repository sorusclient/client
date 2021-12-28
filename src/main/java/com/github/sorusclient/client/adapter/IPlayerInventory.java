package com.github.sorusclient.client.adapter;

public interface IPlayerInventory {

    Slot getSelectedSlot();

    IItem getItem(Slot slot);

    enum Slot {
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE
    }

}
