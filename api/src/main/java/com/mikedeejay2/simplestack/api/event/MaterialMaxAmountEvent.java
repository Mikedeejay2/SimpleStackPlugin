package com.mikedeejay2.simplestack.api.event;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Simple Stack event. Called whenever the <code>getMaxStackSize()</code> method is called on an <code>ItemStack</code>.
 * <p>
 * Similar to {@link ItemStackMaxAmountEvent} but with a {@link Material} instead of an {@link ItemStack}.
 * <p>
 * {@link Material#getMaxStackSize()}, along with the NMS <code>Item</code> counterpart, will call this event.
 *
 * @author Mikedeejay2
 * @since 2.0.0
 */
public class MaterialMaxAmountEvent extends SimpleStackEvent {
    private static final HandlerList handlers = new HandlerList();

    private final @NotNull Material material;
    private int amount;

    public MaterialMaxAmountEvent(@NotNull Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    @NotNull
    public Material getType() {
        return material;
    }

    @NotNull
    public Material getMaterial() {
        return material;
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
