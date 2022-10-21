package com.mikedeejay2.simplestack.api.event;

import com.google.common.base.Preconditions;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlotMaxAmountEvent extends SimpleStackEvent {
    private static final HandlerList handlers = new HandlerList();

    private final @NotNull Inventory inventory;
    private final int slot;
    private int amount;
    private final @Nullable ItemStack itemStack;

    public SlotMaxAmountEvent(@NotNull Inventory inventory, int slot, int amount, @Nullable ItemStack itemStack) {
        this.inventory = inventory;
        this.slot = slot;
        this.amount = amount;
        this.itemStack = itemStack;
    }

    public SlotMaxAmountEvent(@NotNull Inventory inventory, int slot, int amount) {
        this(inventory, slot, amount, null);
    }

    @NotNull
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
        Preconditions.checkArgument(amount > 0 && amount <= 64, "Max amount is out of bounds: %d", amount);
        this.amount = amount;
    }

    @Nullable
    public ItemStack getItemStack() {
        return itemStack;
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
