package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecutterInventory;

/**
 * Miscellaneous utilities for methods that don't fit in any other category.
 *
 * @author Mikedeejay2
 */
public final class StackUtils
{
    /**
     * Returns whether the items between the player's cursor and the inventory slot
     * should switch or not. This needs to be checked because there are rare occasions
     * where an item should not switch (i.e an output slot)
     *
     * @param inventory Inventory that was clicked
     * @param slot      Slot that was clicked
     * @return If items should switch or not
     */
    public static boolean shouldSwitch(Inventory inventory, int slot)
    {
        if(inventory instanceof StonecutterInventory && slot == 1) return false;
        return true;
    }

    /**
     * Gets the max stack amount of an item regardless of whether it's in the config or not.
     * If it's not in the config then return 64.
     *
     * @param item The item to find the max amount for
     * @return The max amount for the item.
     */
    public static int getMaxAmount(Simplestack plugin, ItemStack item)
    {
        return plugin.config().getAmount(item);
    }
}
