package com.mikedeejay2.simplestack.api.event;

import com.google.common.base.Preconditions;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemStackMaxAmountEvent extends SimpleStackEvent {
    private static final HandlerList handlers = new HandlerList();

    private final @NotNull ItemStack itemStack;
    private int amount;

    public ItemStackMaxAmountEvent(@NotNull ItemStack itemStack, int amount) {
        this.itemStack = itemStack;
        this.amount = amount;
    }

    @NotNull
    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        Preconditions.checkArgument(amount > 0 && amount <= 64, "Max amount is out of bounds: %d", amount);
        this.amount = amount;
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
