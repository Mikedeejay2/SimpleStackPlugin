package com.mikedeejay2.simplestack.api.event;

import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class ArmorSlotMaxAmountEvent extends SlotMaxAmountEvent {
    private static final HandlerList handlers = new HandlerList();

    public ArmorSlotMaxAmountEvent(@NotNull Inventory inventory, int slot, int amount) {
        super(inventory, slot, amount);
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
