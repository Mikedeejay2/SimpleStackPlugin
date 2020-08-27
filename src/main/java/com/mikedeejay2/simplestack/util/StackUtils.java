package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.config.ListMode;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class StackUtils
{
    // Plugin instance for referencing
    private static final Simplestack plugin = Simplestack.getInstance();

    // Max stack size. Changing this produces some really weird results because
    // Minecraft really doesn't know how to handle anything higher than 64.
    public static final int MAX_AMOUNT_IN_STACK = 64;

    private static final NamespacedKey key = new NamespacedKey(plugin, "simplestack");

    /**
     * Emulates picking up an item that is regularly unstackable from the ground
     * and attempting to stack it with other items in the player's inventory.
     *
     * @param event The cancellable event that this method has been called in
     * @param groundItem The item on the ground that this method is attempting to move to a player's inventory
     * @param player The player attempting to pick up the groundItem
     * @param item The ItemStack contained inside of the groundItem
     */
    public static void moveItemToInventory(Cancellable event, Item groundItem, Player player, ItemStack item)
    {
        if(item.getType().getMaxStackSize() == 64) return;
        PlayerInventory inv = player.getInventory();
        for(int i = 0; i < inv.getSize(); i++)
        {
            if(!combineItemInternal(item, inv, i)) continue;
            groundItem.remove();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.1f, 1);
            event.setCancelled(true);
            break;
        }
        if(item.getAmount() == 0) return;
        for(int i = 0; i < inv.getSize(); i++)
        {
            if(inv.getItem(i) != null) continue;
            inv.setItem(i, item);
            groundItem.remove();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.1f, 1);
            event.setCancelled(true);
            break;
        }
    }

    /**
     * Moving an item from one inventory to another inventory while manually finding
     * the item in the original inventory and removing it (Mostly for hoppers)
     *
     * @param item The ItemStack being moved
     * @param fromInv The inventory that the items are coming from (source)
     * @param toInv The inventory that the items are moving to (destination)
     * @param amountBeingMoved The amount of items being moves to the toInv
     */
    public static void moveItemToInventory(ItemStack item, Inventory fromInv, Inventory toInv, int amountBeingMoved)
    {
        if(item.getType().getMaxStackSize() == 64) return;
        int amountLeft = amountBeingMoved;
        item = item.clone();
        item.setAmount(amountBeingMoved);
        ItemStack origItem = item.clone();
        Inventory inv = toInv;
        for(int i = 0; i < inv.getSize(); i++)
        {
            if(!combineItemInternal(item, inv, i)) continue;
            amountLeft -= item.getAmount();
            break;
        }
        if(amountBeingMoved != 0)
        {
            for(int i = 0; i < inv.getSize(); i++)
            {
                if(inv.getItem(i) != null) continue;
                inv.setItem(i, item);
                amountLeft = 0;
                break;
            }
        }
        removeItemFromInventory(origItem, fromInv, amountBeingMoved - amountLeft);
    }

    /**
     * Manually remove an item from an inventory if the current slot that the item
     * exists in is unknown.
     *
     * @param item The item to be deleted in the inventory
     * @param inv The inventory that the item is going to be deleted in
     * @param amount The amount of item to be deleted (If unsure, item.getAmount())
     */
    public static void removeItemFromInventory(ItemStack item, Inventory inv, int amount)
    {
        for(int i = 0; i < inv.getSize(); i++)
        {
            ItemStack curItem = inv.getItem(i);
            if(curItem == null) continue;
            if(!equalsEachOther(curItem, item)) continue;
            if(amount > curItem.getAmount())
            {
                amount -= curItem.getAmount();
                curItem.setAmount(0);
            }
            else
            {
                curItem.setAmount(curItem.getAmount() - amount);
                amount = 0;
            }
            inv.setItem(i, curItem);
            if(amount == 0) break;
        }
    }

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
        Sound sound = Sound.BLOCK_ANVIL_USE;
        if(clickedInventory instanceof AnvilInventory && slot == 2)
        {
            triggerAnvilSmithingUse(player, topInv, rightClick, sound);
        }
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
     * Manually update the contents of an anvil or a smithing table. There is a chance that a player
     * added an item to the anvil without updating the contents of the anvil, that is checked with
     * this method. If this method is not called, incorrect values will be displayed to the player.
     *
     * @param topInv Player's top inventory that will be updated
     */
    public static void updateAnvilManual(Inventory topInv)
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
     * Moves an item from one inventory to another (shift-click) while attempting
     * to combine unstackable items.
     *
     * @param itemInSlot Item to be moved (Clicked item)
     * @param clickedInventory The inventory that was clicked
     * @param slot The slot that was clicked
     * @param invToMoveTo The inventory that the item should be moved to
     * @param startingSlot The slot that the algorithm will begin attempting a move at
     * @param endingSlot The slot that the algorithm will stop attempting to move at
     * @param reverse Should the algorithm attempt to move in reverse
     * @return If move was successful
     */
    public static boolean moveItem(ItemStack itemInSlot, Inventory clickedInventory, int slot, Inventory invToMoveTo, int startingSlot, int endingSlot, boolean reverse)
    {
        if(!reverse)
        {
            if(moveItemToExistingStack(itemInSlot, invToMoveTo, startingSlot, endingSlot, false)) return true;
            return moveItemIgnoreStacks(itemInSlot, clickedInventory, slot, invToMoveTo, startingSlot, endingSlot, false);
        }
        else
        {
            if(moveItemToExistingStack(itemInSlot, invToMoveTo, startingSlot, endingSlot, true)) return true;
            return moveItemIgnoreStacks(itemInSlot, clickedInventory, slot, invToMoveTo, startingSlot, endingSlot, true);
        }
    }

    /**
     * Attempt to move an item to a new slot in an inventory while disregarding whether it can stack with
     * other ItemStacks or not.
     *
     * @param itemInSlot The item being moved (Clicked item)
     * @param clickedInventory The inventory that was clicked
     * @param slot The slot that was clicked
     * @param invToMoveTo The inventory that the item should be moved to
     * @param startingSlot The slot that the algorithm will begin attempting a move at
     * @param endingSlot The slot that the algorithm will stop attempting to move at
     * @param reverse Should the algorithm attempt to move in reverse
     * @return If move was successful
     */
    private static boolean moveItemIgnoreStacks(ItemStack itemInSlot, Inventory clickedInventory, int slot, Inventory invToMoveTo, int startingSlot, int endingSlot, boolean reverse)
    {
        if(!reverse)
        {
            for(int i = startingSlot; i < endingSlot; i++)
            {
                if(invToMoveTo.getItem(i) != null) continue;
                invToMoveTo.setItem(i, itemInSlot);
                clickedInventory.setItem(slot, null);
                return true;
            }
        }
        else
        {
            for(int i = endingSlot-1; i >= startingSlot; i--)
            {
                if(invToMoveTo.getItem(i) != null) continue;
                invToMoveTo.setItem(i, itemInSlot);
                clickedInventory.setItem(slot, null);
                return true;
            }
        }
        return false;
    }

    /**
     * Attempt to move an item to an existing stack(s) in an inventory, disregarding any
     * blank space with no items occupying them. This method will only attempt to combine
     * the itemInSlot with any other non-null items in the invToMoveTo.
     *
     * @param itemInSlot The item being moved (Clicked item)
     * @param invToMoveTo The inventory that the item should be moved to
     * @param startingSlot The slot that the algorithm will begin attempting a move at
     * @param endingSlot The slot that the algorithm will stop attempting to move at
     * @param reverse Should the algorithm attempt to move in reverse
     * @return If move was successful
     */
    private static boolean moveItemToExistingStack(ItemStack itemInSlot, Inventory invToMoveTo, int startingSlot, int endingSlot, boolean reverse)
    {
        if(!reverse)
        {
            for(int i = startingSlot; i < endingSlot; i++)
            {
                if(combineItemInternal(itemInSlot, invToMoveTo, i)) break;
            }
        }
        else
        {
            for(int i = startingSlot; i < endingSlot; i++)
            {
                if(combineItemInternal(itemInSlot, invToMoveTo, i)) break;
            }
        }
        return itemInSlot.getAmount() == 0;
    }

    /**
     * Move an item into the player's inventory in the priority that slots 9 to 36 (not the hotbar)
     * are attempted to be moved into first and then the hotbar afterwards.
     *
     * @param itemInSlot The item being moved (Clicked item)
     * @param clickedInventory The inventory that was clicked
     * @param slot The slot that was clicked
     * @param invToMoveTo The inventory that the item should be moved to
     */
    public static void moveItemPlayerOrder(ItemStack itemInSlot, Inventory clickedInventory, int slot, Inventory invToMoveTo)
    {
        if(moveItemToExistingStack(itemInSlot, invToMoveTo, 0, 9, false)) return;
        if(moveItemToExistingStack(itemInSlot, invToMoveTo, 9, 36, false)) return;
        if(!moveItem(itemInSlot, clickedInventory, slot, invToMoveTo, 9, 36, false))
        {
            moveItem(itemInSlot, clickedInventory, slot, invToMoveTo, 0, 9, false);
        }
    }

    /**
     * Attempt to combine an item with another item in a specific slot of an inventory.
     * This method will only be successful if the item slot in the inventory is not null
     * and both the itemInSlot and the item in the inventory slot equal each other (using equalsEachOther())
     *
     * @param itemInSlot The item being moved (Clicked item)
     * @param inv Inventory that will be used to get inventory item
     * @param slot Slot that the method should compare with
     * @return If method was successful
     */
    public static boolean combineItemInternal(ItemStack itemInSlot, Inventory inv, int slot)
    {
        ItemStack itemStack = inv.getItem(slot);
        if(itemStack == null || !equalsEachOther(itemInSlot, itemStack)) return false;
        int newAmount = itemStack.getAmount() + itemInSlot.getAmount();
        int extraAmount = 0;
        if(newAmount > MAX_AMOUNT_IN_STACK)
        {
            extraAmount = (newAmount - MAX_AMOUNT_IN_STACK);
            newAmount = MAX_AMOUNT_IN_STACK;
        }
        itemStack.setAmount(newAmount);
        itemInSlot.setAmount(extraAmount);
        return itemInSlot.getAmount() == 0;
    }

    /**
     * This method is the work around to Minecraft not calling InventoryClickEvents on 2 items
     * of the same type. This is also the reason why this plugin will not work below
     * 1.14 (Because PersistentDataContainer was added in 1.14 spigot). Essentially, this method
     * adds a entry to the NBT data of the clicked item (Key is "simplestack" with the value of 1)
     * to trick the Minecraft client into thinking that this item is unique and sending a packet over
     * to the server saying that they clicked the item and to do something with it.
     *
     * @param itemInSlot The item being moved (Clicked item)
     * @param key The key to be used when setting the NBT data ("simplestack")
     */
    public static void makeUnique(ItemStack itemInSlot, NamespacedKey key)
    {
        ItemMeta itemMeta = itemInSlot.getItemMeta();
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        if(!data.has(key, PersistentDataType.BYTE))
        {
            data.set(key, PersistentDataType.BYTE, (byte) 1);
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
        if(!stack1.getType().equals(stack2.getType())) return false;
        return true;
    }

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
           material.getMaxStackSize() == 64 ||
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
     * By default when a shulker box is broken Minecraft forcefully unstacks any items
     * that don't regularly stack. This can be catastrophic because it can unstack past the amount
     * of inventory slots that a shulker box has resulting in data loss.
     *
     * @param event Event that this method is being run in
     * @param block The block to check
     */
    public static void preserveShulkerBox(BlockBreakEvent event, Block block)
    {
        Location location = block.getLocation();
        World world = location.getWorld();
        ShulkerBox shulkerBox = (ShulkerBox) block.getState();
        ItemStack item = new ItemStack(block.getType());
        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();

        meta.setDisplayName(shulkerBox.getCustomName());
        for(ItemStack curItem : shulkerBox.getInventory().getStorageContents())
        {
            if(curItem == null) continue;
            meta.setBlockState(shulkerBox);
            break;
        }
        item.setItemMeta(meta);
        StackUtils.makeUnique(item, plugin.getKey());

        world.dropItemNaturally(location, item);
        block.setType(Material.AIR);
        event.setCancelled(true);
    }


    /**
     * Moves an entire inventory into the player's inventory.
     * This method is most commonly called when a crafting table / other temporary storage
     * inventory is closed.
     *
     * @param invToMove Inventory that will be moved into the player's inventory
     * @param player The player that will receive the items of the invToMove
     * @param playerInv The inventory of the player
     */
    public static void moveAllItemsToPlayerInv(Inventory invToMove, Player player, Inventory playerInv)
    {
        int startingSlot = 0;
        if(invToMove instanceof CraftingInventory) startingSlot = 1;
        for(int i = startingSlot; i < invToMove.getSize(); i++)
        {
            ItemStack stack = invToMove.getItem(i);
            if(stack == null) continue;

            boolean cancel = StackUtils.cancelStackCheck(stack.getType());
            if(cancel) continue;

            StackUtils.moveItem(stack, invToMove, i, playerInv, 0, 36, false);
            player.updateInventory();
        }
    }
}
