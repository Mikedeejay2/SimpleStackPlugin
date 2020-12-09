package com.mikedeejay2.simplestack.util;

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
        Config config = plugin.config();
        Material material = item.getType();
        if(material == Material.AIR) return true;
        int stackAmount = StackUtils.getMaxAmount(plugin, item);
        if(material.getMaxStackSize() == plugin.config().getMaxAmount() && stackAmount == plugin.config().getMaxAmount())
            return true;
        boolean cancel = false;
        if(plugin.config().getListMode() == ListMode.BLACKLIST)
        {
            if(config.containsMaterial(material)) cancel = true;
        }
        else
        {
            if(!config.containsMaterial(material) && !config.containsItemAmount(material)) cancel = true;
        }
        if(stackAmount == material.getMaxStackSize()) cancel = true;
        if(config.containsUniqueItem(item)) cancel = false;
        return cancel;
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
    public static boolean cancelMoveCheck(Simplestack plugin, ItemStack item, Inventory inv, int slot)
    {
        if(item == null || item.getType() == Material.AIR) return false;
        Material type = item.getType();
        String typeString = type.toString();
        if((inv instanceof PlayerInventory && slot >= 36 && slot <= 39))
        {
            switch(slot)
            {
                case 36: // Boots
                    if(!typeString.endsWith("_BOOTS")) return true;
                    break;
                case 37: // Leggings
                    if(!typeString.endsWith("_LEGGINGS")) return true;
                    break;
                case 38: // Chestplate
                    if(!typeString.endsWith("_CHESTPLATE") && type != Material.ELYTRA) return true;
                    break;
                case 39: // Helmet
                    if(!typeString.endsWith("HELMET")) return true;
                    break;
            }
        }
        if(inv instanceof AbstractHorseInventory && slot == 0 && type != Material.SADDLE) return true;
        if(inv instanceof HorseInventory && slot == 1 && !typeString.endsWith("HORSE_ARMOR")) return true;
        if(inv instanceof CraftingInventory && slot == 0) return true;
        if(inv instanceof FurnaceInventory && slot == 2) return true;
        if(inv instanceof StonecutterInventory && slot == 1) return true;
        if(inv instanceof LoomInventory)
        {
            if(slot == 0 && !typeString.endsWith("BANNER")) return true;
            if(slot == 1 && !typeString.endsWith("DYE")) return true;
            if(slot == 2 && !typeString.endsWith("PATTERN")) return true;
        }
        if(inv instanceof AnvilInventory && slot == 2) return true;
        if(plugin.getMCVersion()[1] >= 16 && inv instanceof SmithingInventory && slot == 2) return true;
        if(inv instanceof GrindstoneInventory && slot == 2) return true;
        if(inv instanceof BrewerInventory)
        {
             if(slot <= 2 && !(typeString.endsWith("BOTTLE") || typeString.endsWith("POTION"))) return true;
             if(slot == 4 && type != Material.BLAZE_POWDER) return true;
        }
        return false;
    }

    /**
     * Cancels a few specific GUI use cases
     *
     * @param inv Inventory to check
     * @return If inventory should be cancelled
     */
    public static boolean cancelGUICheck(Simplestack plugin, Inventory inv, ItemStack cursorItem)
    {
        if(inv == null) return true;
        if(plugin.getMCVersion()[1] >= 16 && inv instanceof SmithingInventory) return false;
        if(cursorItem.getType().toString().endsWith("SHULKER_BOX") && inv.getLocation() != null)
        {
            Location location = inv.getLocation();
            World world = location.getWorld();
            Block block = world.getBlockAt(location);
            Material blockType = block.getType();
            if(blockType.toString().endsWith("SHULKER_BOX")) return true;
        }
        return false;
    }
}
