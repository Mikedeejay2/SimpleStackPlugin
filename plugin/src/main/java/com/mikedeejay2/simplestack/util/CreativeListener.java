package com.mikedeejay2.simplestack.util;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A {@link InventoryCreativeEvent} listener to prevent overstacked creative spawned items from ghosting serverside.
 *
 * @author Mikedeejay2
 */
public final class CreativeListener implements Listener {
    @EventHandler
    public void onCreativeEvent(InventoryCreativeEvent event) {
        final ItemStack itemStack = event.getCursor(); // This will never be null
        final int maxStackSize = itemStack.getMaxStackSize();
        if(itemStack.getAmount() <= maxStackSize) return;

        // Set to the maximum stack size and allow the operation
        itemStack.setAmount(maxStackSize);
        event.setResult(Event.Result.ALLOW);
    }
}
