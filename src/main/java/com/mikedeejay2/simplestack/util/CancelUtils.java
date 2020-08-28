package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.config.ListMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class CancelUtils
{
    private static final Simplestack plugin = Simplestack.getInstance();

    /**
     * Will check to make sure that item being stacked is not blacklisted or not whitelisted
     * or is not null or max stack size is 64 or is of the air material.
     *
     * @param material Material to check
     * @return If stack event should be cancelled
     */
    public static boolean cancelStackCheck(Material material)
    {
        Config config = plugin.getCustomConfig();
        if(material == null ||
                material.equals(Material.AIR))
            return true;
        if(plugin.getCustomConfig().LIST_MODE.equals(ListMode.BLACKLIST))
        {
            return config.LIST.contains(material);
        }
        else
        {
            return !config.LIST.contains(material);
        }
    }

    /**
     * If the player doesn't have the permission "simplestack.use" (enabled by default)
     * then this will tell the event it's being called in to return and not run stacking code.
     *
     * @param player Player to check the permission for
     * @return If the event for the player should be cancelled because they don't have the permission
     */
    public static boolean cancelPlayerCheck(Player player)
    {
        return !player.hasPermission(plugin.getPermission());
    }

    /**
     * Returns whether or not this move should be cancelled or not. Cancels based on the
     * item slot clicked being an output slot or a slot that shouldn't take an item
     * of the clicked type.
     *
     * @param item Item in player's cursor
     * @param inv Inventory being clicked on
     * @param slot Slot that was clicked
     * @return If move should cancel
     */
    public static boolean cancelMoveCursor(ItemStack item, Inventory inv, int slot)
    {
        if(item == null || item.getType().equals(Material.AIR)) return false;
        Material type = item.getType();
        String typeString = type.toString();
        if(inv instanceof AbstractHorseInventory && slot == 0 && !type.equals(Material.SADDLE)) return true;
        if(inv instanceof HorseInventory && slot == 1 && !typeString.endsWith("HORSE_ARMOR")) return true;
        return false;
    }
}
