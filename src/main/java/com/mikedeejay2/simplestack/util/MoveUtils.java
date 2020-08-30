package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.scheduler.BukkitRunnable;

public final class MoveUtils
{
    private static final Simplestack plugin = Simplestack.getInstance();

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
     * Emulates picking up an item that is regularly unstackable from the ground
     * and attempting to stack it with other items in an inventory.
     *
     * @param event The cancellable event that this method has been called in
     * @param groundItem The item on the ground that this method is attempting to move to an inventory
     * @param inventory The inventory that the item is being added to
     * @param item The ItemStack contained inside of the groundItem
     */
    public static void moveItemToInventory(Cancellable event, Item groundItem, Inventory inventory, ItemStack item)
    {
        for(int i = 0; i < inventory.getSize(); i++)
        {
            if(!combineItemInternal(item, inventory, i)) continue;
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
    public static void moveItemToInventory(ItemStack item, Inventory fromInv, Inventory toInv, int amountBeingMoved)
    {
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
            if(!StackUtils.equalsEachOther(curItem, item)) continue;
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

    public static boolean moveItemReverseHotbar(ItemStack itemInSlot, Inventory clickedInventory, int slot, Inventory invToMoveTo)
    {
            if(moveItemToExistingStack(itemInSlot, invToMoveTo, 0, 9, true)) return true;
        if(moveItemToExistingStack(itemInSlot, invToMoveTo, 9, 36, false)) return true;
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
    public static boolean moveItemToExistingStack(ItemStack itemInSlot, Inventory invToMoveTo, int startingSlot, int endingSlot, boolean reverse)
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
        if(itemStack == null || !StackUtils.equalsEachOther(itemInSlot, itemStack)) return false;
        int newAmount = itemStack.getAmount() + itemInSlot.getAmount();
        int extraAmount = 0;
        if(newAmount > Simplestack.MAX_AMOUNT_IN_STACK)
        {
            extraAmount = (newAmount - Simplestack.MAX_AMOUNT_IN_STACK);
            newAmount = Simplestack.MAX_AMOUNT_IN_STACK;
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

            boolean cancel = CancelUtils.cancelStackCheck(stack.getType());
            if(cancel) continue;

            moveItem(stack, invToMove, i, playerInv, 0, 36, false);
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
    public static void dragItemsSurvival(InventoryDragEvent event, InventoryView inventoryView, Player player, ItemStack cursor)
    {
        Integer[] slots = event.getNewItems().keySet().toArray(new Integer[0]);
        ItemStack[] newItems = event.getNewItems().values().toArray(new ItemStack[0]);
        ItemStack[] oldItems = new ItemStack[slots.length];
        for(int i = 0; i < oldItems.length; i++)
        {
            ItemStack oldItem = inventoryView.getItem(slots[i]);
            oldItems[i] = oldItem;
            if(StackUtils.equalsEachOther(cursor, oldItem))
            {
                newItems[i].setAmount(oldItem.getAmount());
            }
            else
            {
                newItems[i].setAmount(0);
            }
        }

        int amountOfItems = newItems.length;
        int cursorSize = cursor.getAmount();
        double amountPerItemRaw = (double)cursorSize/(double)amountOfItems;
        int amountPerItem = (int) Math.floor(amountPerItemRaw);
        int totalAmount = amountPerItem*amountOfItems;
        int amountLeft = cursorSize-totalAmount;

        int newExtraAmount = 0;

        for(int i = 0; i < newItems.length; i++)
        {
            ItemStack item = newItems[i];
            int newAmount = amountPerItem + item.getAmount();
            int extraAmount = 0;
            if(newAmount > Simplestack.MAX_AMOUNT_IN_STACK)
            {
                extraAmount = newAmount % Simplestack.MAX_AMOUNT_IN_STACK;
                newAmount = Simplestack.MAX_AMOUNT_IN_STACK;
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
                newCursor.setAmount(amountLeft+ finalNewExtraAmount);
                player.setItemOnCursor(newCursor);
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
    public static void dragItemsCreative(InventoryDragEvent event, InventoryView inventoryView, Player player, ItemStack cursor)
    {
        Integer[] slots = event.getNewItems().keySet().toArray(new Integer[0]);
        ItemStack[] newItems = event.getNewItems().values().toArray(new ItemStack[0]);
        for(ItemStack item : newItems)
        {
            if(CancelUtils.cancelStackCheck(item.getType())) continue;
            item.setAmount(Simplestack.MAX_AMOUNT_IN_STACK);
        }
        for(int i = 0; i < slots.length; i++)
        {
            int slot = slots[i];
            ItemStack item = newItems[i];
            inventoryView.setItem(slot, item);
        }
    }
}
