package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.BukkitRunnable;

public final class CheckUtils
{
    private static final Simplestack plugin = Simplestack.getInstance();

    /**
     * Check if an anvil has been used. If it has, appropriately calculate the output items.
     * This is required for edge cases like having more items in slot 1 than in slot 2 so that
     * items can't be duped for odd combinations and that output items after the result item has
     * been taken out is also accurate.
     *
     * @param player The player that might be attempting to use the anvil
     * @param topInv The top inventory that the player is viewing
     * @param slot The slot that the player has clicked
     * @param clickedInventory The inventory that the player has clicked
     * @param rightClick Mark if the click was a right click or not to account for resulting in half of the output
     */
    public static void useAnvilCheck(Player player, Inventory topInv, int slot, Inventory clickedInventory, boolean rightClick)
    {
        if(!(clickedInventory instanceof AnvilInventory && slot == 2)) return;
        triggerAnvilSmithingUse(player, topInv, rightClick, Sound.BLOCK_ANVIL_USE);
    }

    /**
     * Check if a stonecutter has been used. If it has, appropriately calculate the output items.
     * This is required because if the item being clicked on is a simplestack item the output has
     * to be calculated manually.
     *
     * @param player The player that might be attempting to use the stonecutter
     * @param topInv The top inventory that the player is viewing
     * @param slot The slot that the player has clicked
     * @param clickedInventory The inventory that the player has clicked
     * @param shiftClick Mark if the click was a shift click or not
     */
    public static void useStonecutterCheck(Player player, Inventory topInv, int slot, Inventory clickedInventory, boolean shiftClick)
    {
        if(!(clickedInventory instanceof StonecutterInventory && slot == 1)) return;
        triggerStonecutterUse(player, topInv, shiftClick);
    }

    /**
     * Trigger the use of a stonecutter. This method appropriately calculates
     * the input and output items
     *
     * @param player The player that might be attempting to use the stonecutter
     * @param topInv The top inventory that the player is viewing
     * @param shiftClick Mark if the click was a shift click or not
     */
    private static void triggerStonecutterUse(Player player, Inventory topInv, boolean shiftClick)
    {
        ItemStack itemInput = topInv.getItem(0);
        ItemStack itemOutput = player.getItemOnCursor().clone();
        itemOutput.setAmount(1);
        if(shiftClick)
        {
            itemOutput.setAmount(itemInput.getAmount());
            itemInput.setAmount(0);
        }
        else
        {
            itemInput.setAmount(itemInput.getAmount()-1);
            itemOutput.setAmount(1);
        }
        topInv.setItem(0, null);
        topInv.setItem(1, null);
        topInv.setItem(0, itemInput);
        topInv.setItem(1, itemOutput);
        player.getWorld().playSound(player.getLocation(), Sound.UI_STONECUTTER_TAKE_RESULT, 1, 1);
    }

    /**
     * Check if an smithing table has been used. If it has, appropriately calculate the output items.
     * This is required for edge cases like having more items in slot 1 than in slot 2 so that
     * items can't be duped for odd combinations and that output items after the result item has
     * been taken out is also accurate.
     *
     * @param player The player that might be attempting to use the smithing table
     * @param topInv The top inventory that the player is viewing
     * @param slot The slot that the player has clicked
     * @param clickedInventory The inventory that the player has clicked
     * @param rightClick Mark if the click was a right click or not to account for resulting in half of the output
     */
    public static void useSmithingCheck(Player player, Inventory topInv, int slot, Inventory clickedInventory, boolean rightClick)
    {
        if(plugin.getMCVersion() < 1.16) return;
        Sound sound = Sound.BLOCK_SMITHING_TABLE_USE;
        if(clickedInventory instanceof SmithingInventory && slot == 2)
        {
            triggerAnvilSmithingUse(player, topInv, rightClick, sound);
        }
    }

    /**
     * Trigger the use of an anvil or a smithing table. This method appropriately calculates
     * the item output amounts of each of the 3 items
     *
     * @param player The player that used the anvil
     * @param topInv The inventory of the anvil
     * @param rightClick Mark if the click was a right click or not to account for resulting in half of the output
     * @param sound The sound that will be played on use
     */
    public static void triggerAnvilSmithingUse(Player player, Inventory topInv, boolean rightClick, Sound sound)
    {
        ItemStack item1 = topInv.getItem(0);
        ItemStack item2 = topInv.getItem(1);
        ItemStack result = topInv.getItem(2);
        double divider = rightClick ? 2 : 1;
        if(result != null)
        {
            if(item2 != null)
            {
                if(item2.getAmount() > item1.getAmount())
                {
                    result.setAmount(item1.getAmount());
                }
                else
                {
                    result.setAmount(item2.getAmount());
                }
                item2.setAmount(item2.getAmount() - (int) Math.ceil(result.getAmount() / divider));
            }
            if(item1 != null) item1.setAmount(item1.getAmount() - (int) Math.ceil(result.getAmount() / divider));
            player.getWorld().playSound(player.getLocation(), sound, 1, 1);
        }
    }

    /**
     * Manually update the contents of an anvil, smithing table, or stonecutter. There is a chance that a player
     * added an item to the GUI or took an item out of the GUI, that is checked with
     * this method. If this method is not called, incorrect values will be displayed to the player and
     * duping could occur.
     *
     * @param topInv Player's top inventory that will be updated
     */
    public static void updateGUIManual(Inventory topInv)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(topInv instanceof AnvilInventory)
                {
                    triggerAnvilSmithingUpdate(topInv);
                }
                if(plugin.getMCVersion() < 1.16) return;
                if(topInv instanceof SmithingInventory)
                {
                    triggerAnvilSmithingUpdate(topInv);
                }
            }
        }.runTask(plugin);
    }

    /**
     * Trigger a manual PrepareAnvilEvent or PrepareSmithingEvent to have the item amounts
     * displayed on the player's screen be correct.
     *
     * @param topInv Player's top inventory that will be updated
     */
    private static void triggerAnvilSmithingUpdate(Inventory topInv)
    {
        ItemStack item1 = topInv.getItem(0);
        ItemStack item2 = topInv.getItem(1);
        topInv.setItem(0, null);
        topInv.setItem(1, null);
        topInv.setItem(0, item1);
        topInv.setItem(1, item2);
    }
}
