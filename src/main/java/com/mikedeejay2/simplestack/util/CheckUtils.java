package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
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
     * Check if the crafting table has been used. If it has, appropriately calculate the output items.
     * This is required because if the item being clicked on is a simplestack item the output has
     * to be calculated manually otherwise duping will happen.
     *
     * @param player The player that might be attempting to use the stonecutter
     * @param topInv The top inventory that the player is viewing
     * @param slot The slot that the player has clicked
     * @param clickedInventory The inventory that the player has clicked
     * @param shiftClick Mark if the click was a shift click or not
     */
    public static void useCraftingTableCheck(Player player, Inventory topInv, int slot, Inventory clickedInventory, boolean shiftClick)
    {
        if(!(clickedInventory instanceof CraftingInventory && slot == 0)) return;
        triggerCraftingTableUse(player, topInv, shiftClick);
    }

    /**
     * Trigger the manual use of a crafting table. This is an algorithm
     * that emulates vanilla's crafting table taking item out of result slot
     * algorithm because by default all events are cancelled. This fixes crafting
     * tables duping items.
     *
     * @param player Player triggering crafting table use
     * @param topInv The top inventory (The crafting table inventory)
     * @param shiftClick If this click is a shift click
     */
    private static void triggerCraftingTableUse(Player player, Inventory topInv, boolean shiftClick)
    {
        int GUISize = topInv.getSize();
        ItemStack resultItem = topInv.getItem(0);
        ItemStack itemInCursor = player.getItemOnCursor();
        if(resultItem == null) return;
        resultItem = resultItem.clone();
        int amountToRemove = 0;
        if(shiftClick)
        {
            int smallestAmount = Integer.MAX_VALUE;
            boolean flag = false;
            for(int i = 1; i < GUISize; i++)
            {
                ItemStack stack = topInv.getItem(i);
                if(stack == null) continue;
                if(stack.getAmount() < smallestAmount)
                {
                    flag = true;
                    smallestAmount = stack.getAmount();
                }
            }
            if(flag)
            {
                ItemStack moveItem = resultItem.clone();
                moveItem.setAmount(smallestAmount-1);
                MoveUtils.moveItem(moveItem, topInv, 0, player.getInventory(), 0, 36, false);
            }
            amountToRemove = smallestAmount;
        }
        else
        {
            amountToRemove = 1;
        }
        for(int i = 1; i < GUISize; i++)
        {
            ItemStack stack = topInv.getItem(i);
            if(stack == null) continue;
            int newAmount = stack.getAmount() - amountToRemove;
            stack.setAmount(newAmount);
        }


        if(StackUtils.equalsEachOther(itemInCursor, resultItem))
        {
            ItemStack newItem = itemInCursor.clone();
            int newAmount = itemInCursor.getAmount() + resultItem.getAmount();
            int extraAmount = 0;
            int maxAmountInStack = StackUtils.getMaxAmount(newItem.getType());
            if(newAmount > maxAmountInStack)
            {
                extraAmount = newAmount % maxAmountInStack;
                newAmount = maxAmountInStack;
            }
            newItem.setAmount(newAmount);
            resultItem.setAmount(extraAmount);
            player.setItemOnCursor(newItem);
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {

                ItemStack tempItem = topInv.getItem(1);
                if(tempItem == null)
                {
                    tempItem = new ItemStack(Material.AIR);
                }
                tempItem = tempItem.clone();
                topInv.setItem(1, null);
                topInv.setItem(1, tempItem);
            }
        }.runTask(plugin);
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
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                ItemStack itemInput = topInv.getItem(0);
                ItemStack itemCursor = player.getItemOnCursor();
                ItemStack itemOutput = topInv.getItem(1);
                if(itemInput == null) return;
                if(shiftClick)
                {
                    if(itemOutput == null) itemOutput = itemCursor.clone();
                    itemOutput.setAmount(itemInput.getAmount());
                    itemInput.setAmount(0);
                }
                else if(itemOutput == null)
                {
                    itemOutput = itemCursor.clone();
                    itemInput.setAmount(itemInput.getAmount()-1);
                    itemOutput.setAmount(1);
                    itemCursor.setAmount(1);
                }
                else
                {
                    itemInput.setAmount(itemInput.getAmount()-1);
                    itemCursor.setAmount(itemCursor.getAmount()+1);
                    itemOutput.setAmount(1);
                }
                player.setItemOnCursor(itemCursor);
                topInv.setItem(0, null);
                topInv.setItem(1, null);
                topInv.setItem(0, itemInput);
                topInv.setItem(1, itemOutput);
                if(itemInput.getAmount() == 0)
                {
                    topInv.setItem(1, null);
                }
                player.getWorld().playSound(player.getLocation(), Sound.UI_STONECUTTER_TAKE_RESULT, 1, 1);
            }
        }.runTask(plugin);
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
            player.sendMessage("eoijeoijeoijeioe");
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

    /**
     * Check if a GUI has been used. This has to be done manually so that the items from
     * the GUI output are calculated properly.
     *
     * @param player The player activating the GUI
     * @param topInv The player's top inventory
     * @param slot The clicked slot
     * @param clickedInventory The clicked Inventory
     * @param clickType The clicktype for calculations
     */
    public static void useGUICheck(Player player, Inventory topInv, int slot, Inventory clickedInventory, ClickType clickType)
    {
        boolean shiftClick = clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT;
        boolean rightClick = clickType == ClickType.RIGHT;
        useAnvilCheck(player, topInv, slot, clickedInventory, rightClick);
        useSmithingCheck(player, topInv, slot, clickedInventory, rightClick);
        useStonecutterCheck(player, topInv, slot, clickedInventory, shiftClick);
        useCraftingTableCheck(player, topInv, slot, clickedInventory, shiftClick);
        useGrindstoneCheck(player, topInv, slot, clickedInventory);
        useBrewingCheck(player, topInv, slot, clickedInventory);
    }

    /**
     * Check to see if a result in the grindstone has been taken. If so, remove
     * the items in the input slots.
     *
     * @param player Player activating the grindstone
     * @param topInv The top inventory (Grindstone inventory)
     * @param slot The slot that has been clicked
     * @param clickedInventory The clicked inventory
     */
    public static void useGrindstoneCheck(Player player, Inventory topInv, int slot, Inventory clickedInventory)
    {
        if(!(clickedInventory instanceof GrindstoneInventory && slot == 2)) return;
        topInv.setItem(0, null);
        topInv.setItem(1, null);
    }

    public static void useBrewingCheck(Player player, Inventory topInv, int slot, Inventory clickedInventory)
    {
        if(!(clickedInventory instanceof BrewerInventory && slot <= 2)) return;
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                ItemStack itemInSlot = clickedInventory.getItem(slot);
                ItemStack itemInCursor = player.getItemOnCursor();
                if(itemInSlot == null) return;

                int newAmount = itemInSlot.getAmount()-1 + itemInCursor.getAmount();

                if(itemInCursor.getType() == Material.AIR)
                {
                    itemInCursor = itemInSlot.clone();
                }
                itemInCursor.setAmount(newAmount);
                itemInSlot.setAmount(1);
                topInv.setItem(slot, itemInSlot);
                player.setItemOnCursor(itemInCursor);
            }
        }.runTask(plugin);
    }
}
