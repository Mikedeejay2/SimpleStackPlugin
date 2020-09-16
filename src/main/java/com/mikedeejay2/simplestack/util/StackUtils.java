package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class StackUtils
{
    // Plugin instance for referencing
    private static final Simplestack plugin = Simplestack.getInstance();

    private static final NamespacedKey key = new NamespacedKey(plugin, "simplestack");

    /**
     * Simple helper method that takes 2 item metas and checks to see if they equal each other.
     *
     * @param stack1 First ItemStack to check
     * @param stack2 Second ItemStack to compare with
     * @return If items are equal
     */
    public static boolean equalsEachOther(ItemStack stack1, ItemStack stack2)
    {
        ItemMeta meta1 = stack1.getItemMeta();
        ItemMeta meta2 = stack2.getItemMeta();
        if(meta1 == null || meta2 == null) return false;
        if(!meta1.equals(meta2)) return false;
        if(stack1.getType() != stack2.getType()) return false;
        return true;
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
    public static boolean shouldSwitch(Inventory inventory, int slot)
    {
        if(inventory instanceof StonecutterInventory && slot == 1) return false;
        return true;
    }

    /**
     * Gets the max stack amount of a material regardless of whether it's in the config or not.
     * If it's not in the config then return 64.
     *
     * @param material The material to find the max amount for
     * @return The max amount for the material.
     */
    public static int getMaxAmount(Material material)
    {
        int maxAmountInStack = Simplestack.getMaxStack();
        if(plugin.config().hasCustomAmount(material))
        {
            maxAmountInStack = plugin.config().getAmount(material);
        }
        return maxAmountInStack;
    }
}
