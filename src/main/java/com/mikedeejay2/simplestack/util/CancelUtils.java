package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.config.ListMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

public final class CancelUtils
{
    private final Simplestack plugin;

    public CancelUtils(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Will check to make sure that item being stacked is not blacklisted or not whitelisted
     * or is not null or max stack size is 64 or is of the air item.
     *
     * @param item Item to check
     * @return If stack event should be cancelled
     */
    public boolean cancelStackCheck(ItemStack item)
    {
        Config config = plugin.config();
        Material material = item.getType();
        if(material == Material.AIR) return true;
        int stackAmount = plugin.stackUtils().getMaxAmount(item);
        if(material.getMaxStackSize() == Simplestack.getMaxStack() && stackAmount == Simplestack.getMaxStack())
            return true;
        boolean cancel = false;
        if(plugin.config().getListMode() == ListMode.BLACKLIST)
        {
            if(config.getMaterialList().contains(material)) cancel = true;
        }
        else
        {
            if(!config.getMaterialList().contains(material)) cancel = true;
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
    public boolean cancelPlayerCheck(Player player)
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
    public boolean cancelMoveCheck(ItemStack item, Inventory inv, int slot)
    {
        if(item == null || item.getType() == Material.AIR) return false;
        Material type = item.getType();
        String typeString = type.toString();
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
     * List of this inventories that this GUI allows through:
     * PlayerInventory - Complete
     * CraftingInventory - Complete
     * GrindstoneInventory - Complete
     * StonecutterInventory - Complete
     * SmithingInventory - Complete
     * AnvilInventory - Complete
     * FurnaceInventory - Complete
     * LoomInventory - Complete
     * AbstractHorseInventory - Complete
     * HorseInventory - Complete
     * CartographyInventory - Complete
     * EnchantingInventory - Complete
     * BeaconInventory - Complete
     * BrewerInventory - Complete
     * @param inv Inventory to check
     * @return If inventory should be cancelled
     */
    public boolean cancelGUICheck(Inventory inv)
    {
        if(inv == null) return true;
        if(plugin.getMCVersion()[1] >= 16 && inv instanceof SmithingInventory) return false;
        if(inv.getLocation() == null) return true;
        return false;
    }
}
