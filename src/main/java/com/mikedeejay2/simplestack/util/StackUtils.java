package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public final class StackUtils
{
    private final Simplestack plugin;

    public StackUtils(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Returns whether the items between the player's cursor and the inventory slot
     * should switch or not. This needs to be checked because there are rare occasions
     * where an item should not switch (i.e an output slot)
     *
     * @param inventory Inventory that was clicked
     * @param slot Slot that was clicked
     * @return If items should switch or not
     */
    public boolean shouldSwitch(Inventory inventory, int slot)
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
    public int getMaxAmount(ItemStack item)
    {
        return plugin.config().getAmount(item);
    }
}
