package com.mikedeejay2.simplestack.api.event;

import org.apache.commons.lang3.Validate;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ItemStackMaxAmountEvent extends SimpleStackEvent {
    private static final HandlerList handlers = new HandlerList();

    private final ItemStack itemStack;
    private int amount;

    public ItemStackMaxAmountEvent(ItemStack itemStack, int amount) {
        this.itemStack = itemStack;
        this.amount = amount;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        Validate.isTrue(amount > 0 && amount <= 64, "Max amount is out of bounds: %d", amount);
        this.amount = amount;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
