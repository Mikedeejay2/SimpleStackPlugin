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
     * This method is the work around to Minecraft not calling InventoryClickEvents on 2 items
     * of the same type. This is also the reason why this plugin will not work below
     * 1.14 (Because PersistentDataContainer was added in 1.14 spigot). Essentially, this method
     * adds a entry to the NBT data of the clicked item (Key is "simplestack" with the value of 1)
     * to trick the Minecraft client into thinking that this item is unique and sending a packet over
     * to the server saying that they clicked the item and to do something with it.
     *
     * As of 1.2.0, this doesn't need to be used for some reason.
     *
     * @param itemInSlot The item being moved (Clicked item)
     * @param key The key to be used when setting the NBT data ("simplestack")
     */
    @Deprecated
    public static void makeUnique(ItemStack itemInSlot, NamespacedKey key)
    {
        if(itemInSlot.getType().getMaxStackSize() == 64) return;
        ItemMeta itemMeta = itemInSlot.getItemMeta();
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        if(!data.has(key, PersistentDataType.BYTE))
        {
            data.set(key, PersistentDataType.BYTE, (byte) 1);
            itemInSlot.setItemMeta(itemMeta);
        }
    }

    /**
     * If this plugin was used before 1.2.0, some items will be stuck on unique and others will not be unique,
     * to fix this, this method removes the unique tag from an item being moved.
     *
     * @param itemInSlot The item being moved (Clicked item)
     * @param key The key to be used when setting the NBT data ("simplestack")
     */
    public static void removeUnique(ItemStack itemInSlot, NamespacedKey key)
    {
        if(itemInSlot == null || itemInSlot.getType().getMaxStackSize() == 64 || !itemInSlot.hasItemMeta()) return;
        ItemMeta itemMeta = itemInSlot.getItemMeta();
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        if(data.has(key, PersistentDataType.BYTE))
        {
            data.remove(key);
            itemInSlot.setItemMeta(itemMeta);
        }
    }

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
}
