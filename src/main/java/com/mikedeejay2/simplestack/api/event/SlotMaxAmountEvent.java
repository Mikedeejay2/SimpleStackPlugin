package com.mikedeejay2.simplestack.api.event;

import org.apache.commons.lang3.Validate;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SlotMaxAmountEvent extends SimpleStackEvent {
    private static final HandlerList handlers = new HandlerList();

    private final Inventory inventory;
    private final int slot;
    private int amount;
    private final ItemStack itemStack; // TODO: Nullable

    public SlotMaxAmountEvent(Inventory inventory, int slot, int amount, ItemStack itemStack) {
        this.inventory = inventory;
        this.slot = slot;
        this.amount = amount;
        this.itemStack = itemStack;
    }

    public SlotMaxAmountEvent(Inventory inventory, int slot, int amount) {
        this(inventory, slot, amount, null);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getSlot() {
        return slot;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        Validate.isTrue(amount > 0 && amount <= 64, "Max amount is out of bounds: %d", amount);
        this.amount = amount;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}