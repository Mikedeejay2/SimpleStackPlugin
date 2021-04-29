package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.config.ListMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

/**
 * Utilities for cancelling an item movement.
 *
 * @author Mikedeejay2
 */
public final class CancelUtils
{
    /**
     * Will check to make sure that item being stacked is not blacklisted or not whitelisted
     * or is not null or max stack size is 64 or is of the air item.
     *
     * @param item Item to check
     * @return If stack event should be cancelled
     */
    public static boolean cancelStackCheck(Simplestack plugin, ItemStack item)
    {
        if(item == null) return false;
        Config config = plugin.config();
        Material material = item.getType();
        int stackAmount = StackUtils.getMaxAmount(plugin, item);
        if(material.getMaxStackSize() == plugin.config().getMaxAmount() &&
                stackAmount == plugin.config().getMaxAmount())
        {
            return true;
        }
        if(plugin.config().getListMode() == ListMode.BLACKLIST)
        {
            if(config.containsMaterial(material)) return true;
        }
        else if(!config.containsMaterial(material) &&
                !config.containsItemAmount(material) &&
                !config.containsUniqueItem(item))
        {
            return true;
        }
        if(stackAmount == material.getMaxStackSize())
        {
            return true;
        }
        return false;
    }

    /**
     * If the player doesn't have the permission "simplestack.use" (enabled by default)
     * then this will tell the event it's being called in to return and not run stacking code.
     *
     * @param player Player to check the permission for
     * @return If the event for the player should be cancelled because they don't have the permission
     */
    public static boolean cancelPlayerCheck(Simplestack plugin, Player player)
    {
        return !player.hasPermission(plugin.getPermission());
    }
}
