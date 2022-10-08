package com.mikedeejay2.simplestack.api.event;

import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

public class ArmorSlotMaxAmountEvent extends SlotMaxAmountEvent {
    private static final HandlerList handlers = new HandlerList();

    public ArmorSlotMaxAmountEvent(Inventory inventory, int slot, int amount) {
        super(inventory, slot, amount);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
