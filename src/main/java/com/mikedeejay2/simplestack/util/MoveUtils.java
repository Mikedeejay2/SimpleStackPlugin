package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utilities for moving items from one location to another
 *
 * @author Mikedeejay2
 */
public final class MoveUtils
{
    /**
     * A list holding all players whose movements should not be processed
     */
    public static final List<Player> doNotMove = new ArrayList<>();

    /**
     * Emulates picking up an item that is regularly unstackable from the ground
     * and attempting to stack it with other items in the player's inventory.
     *
     * @param event The cancellable event that this method has been called in
     * @param groundItem The item on the ground that this method is attempting to move to a player's inventory
     * @param entity The entity attempting to pick up the item
     * @param item The ItemStack contained inside of the groundItem
     * @return Whether the move was successful or not
     */
    public static boolean moveItemToInventory(Simplestack plugin, Cancellable event, Item groundItem, LivingEntity entity, ItemStack item)
    {
        Inventory inv = ((InventoryHolder)entity).getInventory();
        for(int i = 0; i < inv.getStorageContents().length; i++)
        {
            if(!combineItemInternal(plugin, item, inv, i)) continue;
            groundItem.remove();
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.1f, 1);
            event.setCancelled(true);
            break;
        }
        if(item.getAmount() == 0) return true;
        for(int i = 0; i < inv.getStorageContents().length; i++)
        {
            ItemStack curItem = inv.getItem(i);
            if(curItem != null && curItem.getType() != Material.AIR) continue;
            inv.setItem(i, item);
            groundItem.remove();
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.1f, 1);
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    /**
     * Emulates picking up an item that is regularly unstackable from the ground
     * and attempting to stack it with other items in an inventory.
     *
     * @param event The cancellable event that this method has been called in
     * @param groundItem The item on the ground that this method is attempting to move to an inventory
     * @param inventory The inventory that the item is being added to
     * @param item The ItemStack contained inside of the groundItem
     */
    public static void moveItemToInventory(Simplestack plugin, Cancellable event, Item groundItem, Inventory inventory, ItemStack item)
    {
        for(int i = 0; i < inventory.getSize(); i++)
        {
            if(!combineItemInternal(plugin, item, inventory, i)) continue;
            groundItem.remove();
            event.setCancelled(true);
            break;
        }
        if(item.getAmount() == 0) return;
        for(int i = 0; i < inventory.getSize(); i++)
        {
            if(inventory.getItem(i) != null) continue;
            inventory.setItem(i, item);
            groundItem.remove();
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
    public static void moveItemToInventory(Simplestack plugin, ItemStack item, Inventory fromInv, Inventory toInv, int amountBeingMoved)
    {
        int amountLeft = amountBeingMoved;
        if(item.getType() == Material.AIR) return;
        item = item.clone();
        item.setAmount(amountBeingMoved);
        ItemStack origItem = item.clone();
        boolean valid = false;
        for(int i = 0; i < fromInv.getSize(); ++i)
        {
            ItemStack curItem = fromInv.getItem(i);
            if(curItem == null) continue;
            if(!ItemComparison.equalsEachOther(origItem, curItem) && item.getAmount() >= origItem.getAmount()) continue;
            valid = true;
            break;
        }
        if(!valid) return;
        for(int i = 0; i < toInv.getSize(); ++i)
        {
            if(!combineItemInternal(plugin, item, toInv, i)) continue;
            amountLeft -= item.getAmount();
            break;
        }
        if(amountBeingMoved != 0)
        {
            for(int i = 0; i < toInv.getSize(); i++)
            {
                if(toInv.getItem(i) != null) continue;
                toInv.setItem(i, item);
                amountLeft = 0;
                break;
            }
        }
        removeItemFromInventory(origItem, fromInv, amountBeingMoved - amountLeft);
    }

    /**
     * Move an item to an inventory. The item has no original location.
     *
     * @param item The ItemStack being moved
     * @param inv The inventory to move the item into
     */
    public static void moveItem(Simplestack plugin, ItemStack item, Inventory inv)
    {
        if(item.getType() == Material.AIR) return;
        for(int i = 0; i < inv.getStorageContents().length; ++i)
        {
            if(combineItemInternal(plugin, item, inv, i)) break;
        }
        if(item.getAmount() != 0)
        {
            for(int i = 0; i < inv.getStorageContents().length; ++i)
            {
                if(inv.getItem(i) != null) continue;
                inv.setItem(i, item);
                break;
            }
        }
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
        int newAmount = amount;
        for(int i = 0; i < inv.getSize(); i++)
        {
            ItemStack curItem = inv.getItem(i);
            if(curItem == null) continue;
            if(!ItemComparison.equalsEachOther(curItem, item)) continue;
            if(newAmount > curItem.getAmount())
            {
                newAmount -= curItem.getAmount();
                curItem.setAmount(0);
            }
            else
            {
                curItem.setAmount(curItem.getAmount() - newAmount);
                newAmount = 0;
            }
            inv.setItem(i, curItem);
            if(newAmount == 0) break;
        }
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
    public static boolean moveItem(Simplestack plugin, ItemStack itemInSlot, Inventory clickedInventory, int slot, Inventory invToMoveTo, int startingSlot, int endingSlot, boolean reverse)
    {
        if(!reverse)
        {
            if(moveItemToExistingStack(plugin, itemInSlot, invToMoveTo, startingSlot, endingSlot, false)) return true;
            return moveItemIgnoreStacks(itemInSlot, clickedInventory, slot, invToMoveTo, startingSlot, endingSlot, false);
        }
        else
        {
            if(moveItemToExistingStack(plugin, itemInSlot, invToMoveTo, startingSlot, endingSlot, true)) return true;
            return moveItemIgnoreStacks(itemInSlot, clickedInventory, slot, invToMoveTo, startingSlot, endingSlot, true);
        }
    }

    /**
     * Moves an item from one inventory to the player inventory (shift-click) while attempting
     * to combine unstackable items and reversing the order of the hotbar.
     *
     * @param itemInSlot Item to be moved (Clicked item)
     * @param clickedInventory The inventory that was clicked
     * @param slot The slot that was clicked
     * @param invToMoveTo The inventory that the item should be moved to
     * @return If move was successful
     */
    public static boolean moveItemReverseHotbar(Simplestack plugin, ItemStack itemInSlot, Inventory clickedInventory, int slot, Inventory invToMoveTo)
    {
            if(moveItemToExistingStack(plugin, itemInSlot, invToMoveTo, 0, 9, true)) return true;
        if(moveItemToExistingStack(plugin, itemInSlot, invToMoveTo, 9, 36, false)) return true;
        if(moveItemIgnoreStacks(itemInSlot, clickedInventory, slot, invToMoveTo, 0, 9, true)) return true;
        return moveItemIgnoreStacks(itemInSlot, clickedInventory, slot, invToMoveTo, 9, 36, false);
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
    public static boolean moveItemIgnoreStacks(ItemStack itemInSlot, Inventory clickedInventory, int slot, Inventory invToMoveTo, int startingSlot, int endingSlot, boolean reverse)
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
    public static boolean moveItemToExistingStack(Simplestack plugin, ItemStack itemInSlot, Inventory invToMoveTo, int startingSlot, int endingSlot, boolean reverse)
    {
        if(!reverse)
        {
            for(int i = startingSlot; i < endingSlot; i++)
            {
                if(combineItemInternal(plugin, itemInSlot, invToMoveTo, i)) break;
            }
        }
        else
        {
            for(int i = startingSlot; i < endingSlot; i++)
            {
                if(combineItemInternal(plugin, itemInSlot, invToMoveTo, i)) break;
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
    public static void moveItemPlayerOrder(Simplestack plugin, ItemStack itemInSlot, Inventory clickedInventory, int slot, Inventory invToMoveTo)
    {
        if(moveItemToExistingStack(plugin, itemInSlot, invToMoveTo, 0, 9, false)) return;
        if(moveItemToExistingStack(plugin, itemInSlot, invToMoveTo, 9, 36, false)) return;
        if(!moveItem(plugin, itemInSlot, clickedInventory, slot, invToMoveTo, 9, 36, false))
        {
            moveItem(plugin, itemInSlot, clickedInventory, slot, invToMoveTo, 0, 9, false);
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
    public static boolean combineItemInternal(Simplestack plugin, ItemStack itemInSlot, Inventory inv, int slot)
    {
        ItemStack itemStack = inv.getItem(slot);
        if(itemStack == null || !ItemComparison.equalsEachOther(itemInSlot, itemStack)) return false;
        int newAmount = itemStack.getAmount() + itemInSlot.getAmount();
        int extraAmount = 0;
        int maxAmountInStack = StackUtils.getMaxAmount(plugin, itemStack);
        if(newAmount > maxAmountInStack)
        {
            extraAmount = (newAmount - maxAmountInStack);
            newAmount = maxAmountInStack;
        }
        itemStack.setAmount(newAmount);
        itemInSlot.setAmount(extraAmount);
        return itemInSlot.getAmount() == 0;
    }

    /**
     * By default when a shulker box is broken Minecraft forcefully unstacks any items
     * that don't regularly stack. This can be catastrophic because it can unstack past the amount
     * of inventory slots that a shulker box has resulting in data loss.
     *
     * @param event Event that this method is being run in
     * @param block The block to check
     */
    public static void preserveShulkerBox(Block block)
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

        world.dropItemNaturally(location, item);
        block.setType(Material.AIR);
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
    public static void moveAllItemsToPlayerInv(Simplestack plugin, Inventory invToMove, Player player, Inventory playerInv)
    {
        int startingSlot = 0;
        if(invToMove instanceof CraftingInventory) startingSlot = 1;
        for(int i = startingSlot; i < invToMove.getSize(); i++)
        {
            ItemStack stack = invToMove.getItem(i);
            if(stack == null) continue;

            boolean cancel = CancelUtils.cancelStackCheck(plugin, stack);
            if(cancel) continue;

            moveItem(plugin, stack, invToMove, i, playerInv, 0, 36, false);
            player.updateInventory();
        }
    }

    /**
     * Emulates dragging items in an inventory in survival mode. Uses a different algorithm because of the
     * way that items have to combine together, still looks like the vanilla algorithm though.
     *
     * @param event The InventoryDragEvent that this method is being run from
     * @param inventoryView The inventoryView of the player
     * @param player The player dragging the items
     * @param cursor The cursor ItemStack for modification
     */
    public static void dragItemsSurvival(Simplestack plugin, InventoryDragEvent event, InventoryView inventoryView, Player player, ItemStack cursor)
    {
        doNotMove.add(player);
        Integer[] slots = event.getNewItems().keySet().toArray(new Integer[0]);
        ItemStack[] newItems = event.getNewItems().values().toArray(new ItemStack[0]);
        for(int i = 0; i < slots.length; i++)
        {
            ItemStack oldItem = inventoryView.getItem(slots[i]);
            int amount;
            if(oldItem == null) amount = 0;
            else amount = oldItem.getAmount();
            if(oldItem != null && ItemComparison.equalsEachOther(cursor, oldItem))
            {
                newItems[i].setAmount(amount);
            }
            else
            {
                newItems[i].setAmount(0);
            }
        }

        int amountOfItems = newItems.length;
        int cursorSize = cursor.getAmount();
        double amountPerItemRaw = (double)cursorSize / (double)amountOfItems;
        int amountPerItem = (int) Math.floor(amountPerItemRaw);
        int totalAmount = amountPerItem*amountOfItems;
        int amountLeft = cursorSize-totalAmount;

        int newExtraAmount = 0;

        for(int i = 0; i < newItems.length; i++)
        {
            ItemStack item = newItems[i];
            int newAmount = amountPerItem + item.getAmount();
            int extraAmount = 0;
            int maxAmountInStack = StackUtils.getMaxAmount(plugin, item);
            if(newAmount > maxAmountInStack)
            {
                extraAmount = newAmount % maxAmountInStack;
                newAmount = maxAmountInStack;
            }
            item.setAmount(newAmount);
            newExtraAmount += extraAmount;
            inventoryView.setItem(slots[i], item);
        }

        int finalNewExtraAmount = newExtraAmount;
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                ItemStack newCursor = cursor.clone();
                newCursor.setAmount(amountLeft + finalNewExtraAmount);
                player.setItemOnCursor(newCursor);
                doNotMove.remove(player);
            }
        }.runTask(plugin);
    }

    /**
     * Emulates dragging items in an inventory in creative mode. Uses a different algorithm because of the
     * way that items have to combine together, still looks like the vanilla algorithm though.
     *
     * @param event The InventoryDragEvent that this method is being run from
     * @param inventoryView The inventoryView of the player
     * @param player The player dragging the items
     * @param cursor The cursor ItemStack for modification
     */
    public static void dragItemsCreative(Simplestack plugin, InventoryDragEvent event, InventoryView inventoryView, Player player, ItemStack cursor)
    {
        Integer[] slots = event.getNewItems().keySet().toArray(new Integer[0]);
        ItemStack[] newItems = event.getNewItems().values().toArray(new ItemStack[0]);
        for(ItemStack item : newItems)
        {
            if(CancelUtils.cancelStackCheck(plugin, item)) continue;
            int maxAmountInStack = StackUtils.getMaxAmount(plugin, item);
            item.setAmount(maxAmountInStack);
        }
        for(int i = 0; i < slots.length; i++)
        {
            int slot = slots[i];
            ItemStack item = newItems[i];
            inventoryView.setItem(slot, item);
        }
    }

    /**
     * A method that attempts to combine an inputStack into the targetStack
     *
     * @param inputStack The input stack
     * @param targetStack The target stack (result stack)
     */
    public static void mergeItems(Simplestack plugin, ItemStack inputStack, ItemStack targetStack)
    {
        int maxAmountInStack = StackUtils.getMaxAmount(plugin, inputStack);
        int newAmount = inputStack.getAmount() + targetStack.getAmount();
        int extraAmount = 0;
        if(newAmount > maxAmountInStack)
        {
            extraAmount = (newAmount - maxAmountInStack);
            newAmount = maxAmountInStack;
        }
        inputStack.setAmount(extraAmount);
        targetStack.setAmount(newAmount);
    }
}
