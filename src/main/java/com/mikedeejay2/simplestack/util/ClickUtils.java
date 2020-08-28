package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.BukkitRunnable;

public class ClickUtils
{
    private static final Simplestack plugin = Simplestack.getInstance();

    /**
     * Emulates a left click event that includes stacking items together that
     * regularly wouldn't be stacked.
     *
     * @param itemInSlot The item clicked on by the cursor
     * @param itemInCursor The item currently in the cursor (if any)
     * @param player The player that has clicked
     * @param event The InventoryClickEvent that this method was called from
     */
    public static void leftClick(ItemStack itemInSlot, ItemStack itemInCursor, Player player, InventoryClickEvent event)
    {
        Inventory clickedInv = event.getClickedInventory();
        int slot = event.getSlot();
        Inventory topInv = player.getOpenInventory().getTopInventory();
        if(cancelMoveCursor(itemInCursor, clickedInv, slot)) return;
        if(!StackUtils.equalsEachOther(itemInCursor, itemInSlot))
        {
            StackUtils.useSmithingCheck(player, topInv, slot, clickedInv, false);
            StackUtils.useAnvilCheck(player, topInv, slot, clickedInv, false);

            player.setItemOnCursor(itemInSlot);
            clickedInv.setItem(slot, itemInCursor);

            StackUtils.useStonecutterCheck(player, topInv, slot, clickedInv, false);
            player.updateInventory();
            return;
        }

        int newAmount = itemInCursor.getAmount() + itemInSlot.getAmount();
        int extraAmount = 0;
        if(newAmount > StackUtils.MAX_AMOUNT_IN_STACK)
        {
            extraAmount = (newAmount - StackUtils.MAX_AMOUNT_IN_STACK);
            newAmount = StackUtils.MAX_AMOUNT_IN_STACK;
        }
        itemInCursor.setAmount(newAmount);
        itemInSlot.setAmount(extraAmount);
        if(shouldSwitch(clickedInv, slot))
        {
            clickedInv.setItem(slot, itemInCursor);
            player.getOpenInventory().setCursor(itemInSlot);
        }
        else
        {
            clickedInv.setItem(slot, itemInSlot);
            player.setItemOnCursor(itemInCursor);
            StackUtils.useStonecutterCheck(player, topInv, slot, clickedInv, false);
        }
        player.updateInventory();
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
    private static boolean cancelMoveCursor(ItemStack item, Inventory inv, int slot)
    {
        if(item == null || item.getType().equals(Material.AIR)) return false;
        Material type = item.getType();
        String typeString = type.toString();
        if(inv instanceof AbstractHorseInventory && slot == 0 && !type.equals(Material.SADDLE)) return true;
        if(inv instanceof HorseInventory && slot == 1 && !typeString.endsWith("HORSE_ARMOR")) return true;
        return false;
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

    /**
     * Emulates a right click event that includes combining and stacking items together that
     * regularly wouldn't be stacked.
     *
     * @param itemInSlot The item clicked on by the cursor
     * @param itemInCursor The item currently in the cursor (if any)
     * @param player The player that has clicked
     * @param event The InventoryClickEvent that this method was called from
     */
    public static void rightClick(ItemStack itemInSlot, ItemStack itemInCursor, Player player, InventoryClickEvent event)
    {
        Inventory topInv = player.getOpenInventory().getTopInventory();
        int slot = event.getSlot();
        Inventory clickedInv = event.getClickedInventory();
        if(cancelMoveCursor(itemInCursor, clickedInv, slot)) return;
        if(!StackUtils.equalsEachOther(itemInCursor, itemInSlot))
        {
            StackUtils.useSmithingCheck(player, topInv, slot, clickedInv, true);
            StackUtils.useAnvilCheck(player, topInv, slot, clickedInv, true);

            if(itemInSlot.getType().equals(Material.AIR) && !itemInCursor.getType().equals(Material.AIR))
            {
                itemInSlot = itemInCursor.clone();
                itemInSlot.setAmount(1);
                itemInCursor.setAmount(itemInCursor.getAmount()-1);
                clickedInv.setItem(slot, itemInSlot);
            }
            else
            {
                ItemStack cursorItemStack = itemInSlot.clone();
                cursorItemStack.setAmount((int) Math.ceil(itemInSlot.getAmount() / 2.0f));
                itemInSlot.setAmount((int) Math.floor(itemInSlot.getAmount() / 2.0f));
                player.setItemOnCursor(cursorItemStack);
            }

            StackUtils.useStonecutterCheck(player, topInv, slot, clickedInv, false);
            player.updateInventory();
            return;
        }

        if(itemInCursor.getAmount() > 0)
        {
            int bottomAmount = itemInSlot.getAmount() + 1;
            int topAmount = itemInCursor.getAmount() - 1;
            itemInSlot.setAmount(bottomAmount);
            itemInCursor.setAmount(topAmount);
        }
        player.updateInventory();
    }


    /**
     * Emulates a shift click event that includes moving an item from one inventory to another
     * inventory while taking into account that the item is stacked and can combine with other
     * unstackable items.
     *
     * @param itemInSlot The item clicked on by the cursor
     * @param player The player that has clicked
     * @param event The InventoryClickEvent that this method was called from
     */
    public static void shiftClick(ItemStack itemInSlot, Player player, InventoryClickEvent event)
    {
        Inventory inv = null;
        Inventory topInv = player.getOpenInventory().getTopInventory();
        Inventory bottomInv = player.getOpenInventory().getBottomInventory();
        int slot = event.getSlot();
        if(!(bottomInv instanceof PlayerInventory) || !(topInv instanceof CraftingInventory && topInv.getSize() == 5))
        {
            shiftClickSeperateInv(itemInSlot, event, inv, topInv, bottomInv, slot, player);
        }
        else
        {
            shiftClickSameInv(itemInSlot, event, bottomInv);
        }
        player.updateInventory();
        event.setCancelled(true);
    }

    /**
     * Emulates shift clicking an item into a different inventory than the current inventory
     * taking into account that the item can stack with other unstackable items and different
     * behaviors of different GUIs
     *
     * @param itemInSlot The item clicked on by the cursor
     * @param event The InventoryClickEvent that this method was called from
     * @param inv The Inventory that was clicked on (method reassigns this to the inventory that the items moves to)
     * @param topInv The top inventory that the player is viewing
     * @param bottomInv The bottom inventory that the player is viewing
     * @param slot The slot that the player has clicked on
     */
    private static void shiftClickSeperateInv(ItemStack itemInSlot, InventoryClickEvent event, Inventory inv, Inventory topInv, Inventory bottomInv, int slot, Player player)
    {
        Inventory clickedInventory = event.getClickedInventory();
        if(clickedInventory.equals(bottomInv))
        {
            if(topInv instanceof CraftingInventory && topInv.getSize() == 5)
            {
                inv = bottomInv;
            }
            else
            {
                inv = topInv;
            }
        }
        else if(clickedInventory.equals(topInv))
        {
            inv = bottomInv;
        }

        int startSlot = 0;
        int endSlot = inv.getSize();
        boolean reverse = false;
        boolean playerOrder = false;
        boolean reverseHotbar = false;
        if(inv instanceof PlayerInventory)
        {
            endSlot -= 5;

            if(topInv instanceof GrindstoneInventory)
            {
                topInv.setItem(0, null);
                topInv.setItem(1, null);
            }
            StackUtils.useStonecutterCheck(player, topInv, slot, clickedInventory, true);
        }
        else if(inv instanceof CraftingInventory)
        {
            ++startSlot;
        }
        else if(inv instanceof FurnaceInventory)
        {
            if(itemInSlot.getType().isFuel())
            {
                --endSlot;
                reverse = true;
            }
        }
        else if(inv instanceof EnchantingInventory)
        {
            --endSlot;
            if(inv.getItem(0) != null) return;
            ItemStack itemToMove = itemInSlot.clone();
            itemToMove.setAmount(1);
            StackUtils.moveItem(itemToMove, clickedInventory, slot, inv, startSlot, endSlot, reverse);
            itemInSlot.setAmount(itemInSlot.getAmount()-1);
            clickedInventory.setItem(slot, itemInSlot);
            return;
        }
        else if(inv instanceof LoomInventory)
        {
            if(itemInSlot.getType().toString().endsWith("BANNER"))
            {
                startSlot = 0;
                endSlot = 1;
            }
            else if(itemInSlot.getType().toString().endsWith("PATTERN"))
            {
                startSlot = 2;
                endSlot = 3;
            }
            else
            {
                ClickUtils.shiftClickSameInv(itemInSlot, event, bottomInv);
                return;
            }
        }
        else if(inv instanceof CartographyInventory)
        {
            if(itemInSlot.getType().equals(Material.FILLED_MAP))
            {
                startSlot = 0;
                endSlot = 1;
            }
            else if(itemInSlot.getType().equals(Material.PAPER))
            {
                startSlot = 1;
                endSlot = 2;
            }
            else
            {
                ClickUtils.shiftClickSameInv(itemInSlot, event, bottomInv);
                return;
            }
        }
        else if(inv instanceof AbstractHorseInventory)
        {
            if(itemInSlot.getType().equals(Material.SADDLE))
            {
                startSlot = 0;
                endSlot = 1;
            }
            else if(itemInSlot.getType().toString().endsWith("HORSE_ARMOR") && inv instanceof HorseInventory)
            {
                startSlot = 1;
                endSlot = 2;
            }
            else
            {
                ClickUtils.shiftClickSameInv(itemInSlot, event, bottomInv);
                return;
            }
        }
        if(playerOrder)
        {
            StackUtils.moveItemPlayerOrder(itemInSlot, clickedInventory, slot, bottomInv);
        }
        else
        {
            StackUtils.moveItem(itemInSlot, clickedInventory, slot, inv, startSlot, endSlot, reverse);
        }
    }

    /**
     * Emulates shift clicking in the same inventory, this occurs when the player shift clicks in the survival
     * inventory when not viewing another inventory or when attempting to shift click an item into another inventory
     * like a furnace that will not accept the item being shift clicked in any GUI slot.
     *
     * @param itemInSlot The item clicked on by the cursor
     * @param event The InventoryClickEvent that this method was called from
     * @param bottomInv The inventory that will be used (This method only uses the player's inventory)
     */
    private static void shiftClickSameInv(ItemStack itemInSlot, InventoryClickEvent event, Inventory bottomInv)
    {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory inv;
        int slot = event.getSlot();
        inv = event.getClickedInventory();
        String type = itemInSlot.getType().toString();
        if(inv instanceof CraftingInventory)
        {
            StackUtils.moveItemPlayerOrder(itemInSlot, clickedInventory, slot, bottomInv);
            return;
        }
        if(!type.endsWith("_HELMET") &&
                !type.endsWith("_CHESTPLATE") &&
                !type.endsWith("_LEGGINGS") &&
                !type.endsWith("_BOOTS") &&
                !type.equals("SHIELD") &&
                !type.equals("ELYTRA"))
        {
            if(slot < 9)
            {
                StackUtils.moveItem(itemInSlot, clickedInventory, slot, inv, 9, 36, false);
            }
            else if(slot < 36)
            {
                StackUtils.moveItem(itemInSlot, clickedInventory, slot, inv, 0, 9, false);
            }
            else
            {
                StackUtils.moveItem(itemInSlot, clickedInventory, slot, inv, 9, 36, false);
            }
        }
        else
        {
            if(slot < 36)
            {
                if(type.endsWith("_BOOTS"))
                {
                    inv.setItem(36, itemInSlot);
                    inv.setItem(slot, null);
                }
                else if(type.endsWith("_LEGGINGS"))
                {
                    inv.setItem(37, itemInSlot);
                    inv.setItem(slot, null);
                }
                else if(type.endsWith("_CHESTPLATE") || type.equals("ELYTRA"))
                {
                    inv.setItem(38, itemInSlot);
                    inv.setItem(slot, null);
                }
                else if(type.endsWith("_HELMET"))
                {
                    inv.setItem(39, itemInSlot);
                    inv.setItem(slot, null);
                }
                else if(type.equals("SHIELD"))
                {
                    inv.setItem(40, itemInSlot);
                    inv.setItem(slot, null);
                }
            }
            else
            {
                StackUtils.moveItem(itemInSlot, clickedInventory, slot, inv, 9, 36, false);
            }
        }
    }

    /**
     * Emulates specifically picking up all of an item.
     * Probably not used, backup method if something major breaks and I don't have time to fix it.
     *
     * @param player The player that has clicked
     * @param itemInSlot The item clicked on by the cursor
     * @param inventoryView The player's inventory view
     * @param rawSlot The raw inventory slot that has been clicked on
     */
    public static void pickupAll(Player player, ItemStack itemInSlot, InventoryView inventoryView, int rawSlot)
    {
        player.setItemOnCursor(itemInSlot);
        inventoryView.setItem(rawSlot, null);
    }

    /**
     * Emulates specifically placing everything in the cursor into the slot below.
     *
     * @param player The player that has clicked
     * @param itemInSlot The item clicked on by the cursor
     * @param itemInCursor The item currently in the cursor (if any)
     * @param inventoryView The player's inventory view
     * @param rawSlot The raw inventory slot that has been clicked on
     */
    public static void placeAll(Player player, ItemStack itemInSlot, ItemStack itemInCursor, InventoryView inventoryView, int rawSlot)
    {
        int newAmount = itemInSlot.getAmount() + itemInCursor.getAmount();
        int extraAmount = 0;
        if(newAmount > StackUtils.MAX_AMOUNT_IN_STACK)
        {
            extraAmount = newAmount % StackUtils.MAX_AMOUNT_IN_STACK;
            newAmount = StackUtils.MAX_AMOUNT_IN_STACK;
        }
        itemInCursor.setAmount(newAmount);
        itemInSlot.setAmount(extraAmount);
        inventoryView.setItem(rawSlot, itemInCursor);
        player.setItemOnCursor(itemInSlot);
    }

    /**
     * Emulates specifically picking up half of an ItemStack into the cursor.
     * Probably not used, backup method if something major breaks and I don't have time to fix it.
     *
     * @param player The player that has clicked
     * @param itemInSlot The item clicked on by the cursor
     * @param inventoryView The player's inventory view
     * @param rawSlot The raw inventory slot that has been clicked on
     */
    public static void pickupHalf(Player player, ItemStack itemInSlot, InventoryView inventoryView, int rawSlot)
    {
        ItemStack itemPutDown;
        int totalAmount = itemInSlot.getAmount();
        itemInSlot.setAmount((int) Math.ceil(totalAmount/2.0));
        itemPutDown = itemInSlot.clone();
        itemPutDown.setAmount((int) Math.floor(totalAmount/2.0));
        inventoryView.setItem(rawSlot, itemPutDown);
        player.setItemOnCursor(itemInSlot);
    }

    /**
     * Emulates cloning a stack of items (creative mode)
     * This method will force a cloned stack's new size to be 64.
     *
     * @param player The player that has clicked
     * @param itemInSlot The item clicked on by the cursor
     */
    public static void cloneStack(Player player, ItemStack itemInSlot)
    {
        ItemStack itemPutDown;
        itemPutDown = itemInSlot.clone();
        itemPutDown.setAmount(StackUtils.MAX_AMOUNT_IN_STACK);
        player.setItemOnCursor(itemPutDown);
    }

    /**
     * Emulates dragging items in an inventory. Uses a different algorithm because of the
     * way that items have to combine together, still looks like the vanilla algorithm though.
     *
     * @param event The InventoryDragEvent that this method is being run from
     * @param inventoryView The inventoryView of the player
     * @param player The player dragging the items
     * @param cursor The cursor ItemStack for modification
     */
    public static void dragItems(InventoryDragEvent event, InventoryView inventoryView, Player player, ItemStack cursor)
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
            if(newAmount > StackUtils.MAX_AMOUNT_IN_STACK)
            {
                extraAmount = newAmount % StackUtils.MAX_AMOUNT_IN_STACK;
                newAmount = StackUtils.MAX_AMOUNT_IN_STACK;
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
}
