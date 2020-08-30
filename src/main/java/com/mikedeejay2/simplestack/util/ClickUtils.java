package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;

public final class ClickUtils
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
        if(CancelUtils.cancelMoveCheck(itemInCursor, clickedInv, slot)) return;
        if(!StackUtils.equalsEachOther(itemInCursor, itemInSlot))
        {
            player.setItemOnCursor(itemInSlot);
            clickedInv.setItem(slot, itemInCursor);
            player.updateInventory();
            return;
        }

        int newAmount = itemInCursor.getAmount() + itemInSlot.getAmount();
        int extraAmount = 0;
        if(newAmount > Simplestack.MAX_AMOUNT_IN_STACK)
        {
            extraAmount = (newAmount - Simplestack.MAX_AMOUNT_IN_STACK);
            newAmount = Simplestack.MAX_AMOUNT_IN_STACK;
        }
        itemInCursor.setAmount(newAmount);
        itemInSlot.setAmount(extraAmount);
        if(StackUtils.shouldSwitch(clickedInv, slot))
        {
            clickedInv.setItem(slot, itemInCursor);
            player.getOpenInventory().setCursor(itemInSlot);
        }
        else
        {
            clickedInv.setItem(slot, itemInSlot);
            player.setItemOnCursor(itemInCursor);
            CheckUtils.useStonecutterCheck(player, topInv, slot, clickedInv, false);
        }
        player.updateInventory();
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
        if(CancelUtils.cancelMoveCheck(itemInCursor, clickedInv, slot)) return;
        if(!StackUtils.equalsEachOther(itemInCursor, itemInSlot))
        {
            if(itemInSlot.getType() == Material.AIR && itemInCursor.getType() != Material.AIR)
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
     * @param toInv The Inventory that was clicked on (method reassigns this to the inventory that the items moves to)
     * @param topInv The top inventory that the player is viewing
     * @param bottomInv The bottom inventory that the player is viewing
     * @param slot The slot that the player has clicked on
     */
    private static void shiftClickSeperateInv(ItemStack itemInSlot, InventoryClickEvent event, Inventory toInv, Inventory topInv, Inventory bottomInv, int slot, Player player)
    {
        Inventory clickedInventory = event.getClickedInventory();
        if(clickedInventory.equals(bottomInv))
        {
            if(topInv instanceof CraftingInventory && topInv.getSize() == 5)
            {
                toInv = bottomInv;
            }
            else
            {
                toInv = topInv;
            }
        }
        else if(clickedInventory.equals(topInv))
        {
            toInv = bottomInv;
        }

        int startSlot = 0;
        int endSlot = toInv.getSize();
        boolean reverse = false;
        boolean playerOrder = false;
        boolean reverseHotbar = false;
        if(toInv instanceof PlayerInventory)
        {
            endSlot -= 5;

            if(topInv instanceof GrindstoneInventory && slot == 2)
            {
                topInv.setItem(0, null);
                topInv.setItem(1, null);
                ClickUtils.shiftClickSameInv(itemInSlot, event, bottomInv);
                return;
            }
            else if(topInv instanceof CraftingInventory ||
                    topInv instanceof FurnaceInventory ||
                    topInv instanceof AnvilInventory ||
                    (plugin.getMCVersion() >= 1.16 && topInv instanceof SmithingInventory))
            {
                --endSlot;
                playerOrder = true;
            }



        }
        else if(toInv instanceof CraftingInventory)
        {
            ++startSlot;
        }
        else if(toInv instanceof FurnaceInventory)
        {
            --endSlot;
            if(itemInSlot.getType().isFuel())
            {
                reverse = true;
            }
        }
        else if(toInv instanceof EnchantingInventory)
        {
            --endSlot;
            if(toInv.getItem(0) != null) return;
            ItemStack itemToMove = itemInSlot.clone();
            itemToMove.setAmount(1);
            MoveUtils.moveItem(itemToMove, clickedInventory, slot, toInv, startSlot, endSlot, reverse);
            itemInSlot.setAmount(itemInSlot.getAmount()-1);
            clickedInventory.setItem(slot, itemInSlot);
            return;
        }
        else if(toInv instanceof AnvilInventory || (plugin.getMCVersion() >= 1.16 && toInv instanceof SmithingInventory))
        {
            --endSlot;
        }
        else if(toInv instanceof LoomInventory)
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
            else if(itemInSlot.getType().toString().endsWith("DYE"))
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
        else if(toInv instanceof CartographyInventory)
        {
            if(itemInSlot.getType() == Material.FILLED_MAP)
            {
                startSlot = 0;
                endSlot = 1;
            }
            else if(itemInSlot.getType() == Material.PAPER)
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
        else if(toInv instanceof AbstractHorseInventory)
        {
            if(itemInSlot.getType() == Material.SADDLE)
            {
                startSlot = 0;
                endSlot = 1;
            }
            else if(itemInSlot.getType().toString().endsWith("HORSE_ARMOR") && toInv instanceof HorseInventory)
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
        else if(toInv instanceof StonecutterInventory ||
                toInv instanceof GrindstoneInventory)
        {
            --endSlot;
        }
        else if(toInv instanceof BrewerInventory)
        {
            if(itemInSlot.getType().toString().endsWith("BOTTLE") || itemInSlot.getType().toString().endsWith("POTION"))
            {
                ItemStack oldItemSlot = itemInSlot;
                itemInSlot = itemInSlot.clone();
                itemInSlot.setAmount(1);
                for(int i = 0; i < 3; i++)
                {
                    if(oldItemSlot.getAmount() == 0) break;
                    if(toInv.getItem(i) != null) continue;
                    oldItemSlot.setAmount(oldItemSlot.getAmount()-1);
                    clickedInventory.setItem(slot, oldItemSlot);
                    toInv.setItem(i, itemInSlot);
                }
                return;
            }
            else if(itemInSlot.getType() == Material.BLAZE_POWDER)
            {
                startSlot = 3;
                endSlot = 5;
                reverse = true;
            }
            else
            {
                startSlot = 3;
                endSlot = 4;
            }
        }
        else if(toInv instanceof BeaconInventory)
        {
            Material type = itemInSlot.getType();
            if((type != Material.IRON_INGOT &&
                type != Material.GOLD_INGOT &&
                type != Material.DIAMOND &&
                type != Material.EMERALD &&
                type != Material.NETHERITE_INGOT) ||
                toInv.getItem(0) != null
            )
            {
                ClickUtils.shiftClickSameInv(itemInSlot, event, bottomInv);
                return;
            }
            ItemStack oldItemSlot = itemInSlot;
            itemInSlot = itemInSlot.clone();
            itemInSlot.setAmount(1);
            oldItemSlot.setAmount(oldItemSlot.getAmount()-1);
            clickedInventory.setItem(slot, oldItemSlot);
            toInv.setItem(0, itemInSlot);
            return;
        }



        if(playerOrder)
        {
            MoveUtils.moveItemPlayerOrder(itemInSlot, clickedInventory, slot, bottomInv);
        }
        else
        {
            MoveUtils.moveItem(itemInSlot, clickedInventory, slot, toInv, startSlot, endSlot, reverse);
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
            MoveUtils.moveItemPlayerOrder(itemInSlot, clickedInventory, slot, bottomInv);
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
                MoveUtils.moveItem(itemInSlot, clickedInventory, slot, inv, 9, 36, false);
            }
            else if(slot < 36)
            {
                MoveUtils.moveItem(itemInSlot, clickedInventory, slot, inv, 0, 9, false);
            }
            else
            {
                MoveUtils.moveItem(itemInSlot, clickedInventory, slot, inv, 9, 36, false);
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
                MoveUtils.moveItem(itemInSlot, clickedInventory, slot, inv, 9, 36, false);
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
        if(newAmount > Simplestack.MAX_AMOUNT_IN_STACK)
        {
            extraAmount = newAmount % Simplestack.MAX_AMOUNT_IN_STACK;
            newAmount = Simplestack.MAX_AMOUNT_IN_STACK;
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
        itemPutDown.setAmount(Simplestack.MAX_AMOUNT_IN_STACK);
        player.setItemOnCursor(itemPutDown);
    }
}
