package com.mikedeejay2.simplestack.api.event;

import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;

public class MaterialMaxAmountEvent extends SimpleStackEvent {
    private static final HandlerList handlers = new HandlerList();

    private final Material material;
    private int amount;

    public MaterialMaxAmountEvent(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    public Material getType() {
        return material;
    }

    public Material getMaterial() {
        return material;
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
