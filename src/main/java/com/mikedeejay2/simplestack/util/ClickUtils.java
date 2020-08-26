package com.mikedeejay2.simplestack.util;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;

public class ClickUtils
{
    private static final Simplestack plugin = Simplestack.getInstance();

    /*
     * This helped method is a left click event that attempts to
     * stack 2 items together to a stack of 64.
     */
    public static void leftClick(ItemStack itemPickUp, ItemStack itemPutDown, Player player, InventoryClickEvent event)
    {
        Inventory clickedInv = event.getClickedInventory();
        int slot = event.getSlot();
        Inventory topInv = player.getOpenInventory().getTopInventory();
        if(!StackUtils.equalsEachOther(itemPutDown, itemPickUp))
        {
            StackUtils.useSmithingCheck(player, topInv, slot, clickedInv, false);
            StackUtils.useAnvilCheck(player, topInv, slot, clickedInv, false);
            player.setItemOnCursor(itemPickUp);
            clickedInv.setItem(slot, itemPutDown);
            player.updateInventory();
            return;
        }

        int newAmount = itemPutDown.getAmount() + itemPickUp.getAmount();
        int extraAmount = 0;
        if(newAmount > StackUtils.MAX_AMOUNT_IN_STACK)
        {
            extraAmount = (newAmount - StackUtils.MAX_AMOUNT_IN_STACK);
            newAmount = StackUtils.MAX_AMOUNT_IN_STACK;
        }
        itemPutDown.setAmount(newAmount);
        itemPickUp.setAmount(extraAmount);
        event.getClickedInventory().setItem(event.getSlot(), itemPutDown);
        player.getOpenInventory().setCursor(itemPickUp);
        player.updateInventory();
    }

    /*
     * This helper method attempts to emulate a right click event where
     * one item is removed from the held stack and put down to the slot
     * being right clicked.
     */
    public static void rightClick(ItemStack itemPickUp, ItemStack itemPutDown, Player player, InventoryClickEvent event)
    {
        Inventory topInv = player.getOpenInventory().getTopInventory();
        int slot = event.getSlot();
        Inventory clickedInv = event.getClickedInventory();
        if(!StackUtils.equalsEachOther(itemPutDown, itemPickUp))
        {
            StackUtils.useSmithingCheck(player, topInv, slot, clickedInv, true);
            StackUtils.useAnvilCheck(player, topInv, slot, clickedInv, true);
            ItemStack cursorItemStack = itemPickUp.clone();
            cursorItemStack.setAmount((int) Math.ceil(itemPickUp.getAmount()/2.0f));
            itemPickUp.setAmount((int) Math.floor(itemPickUp.getAmount()/2.0f));
            player.setItemOnCursor(cursorItemStack);
            player.updateInventory();
            return;
        }

        if(itemPutDown.getAmount() > 0)
        {
            int bottomAmount = itemPickUp.getAmount() + 1;
            int topAmount = itemPutDown.getAmount() - 1;
            itemPickUp.setAmount(bottomAmount);
            itemPutDown.setAmount(topAmount);
        }
        player.updateInventory();
    }

    /*
     * This helper method emulates the action of shift clicking an item into another inventory.
     * This is the biggest method because there's a lot that has a possibility
     * of happening when shift clicking.
     */
    public static void shiftClick(ItemStack itemPickUp, Player player, InventoryClickEvent event)
    {
        if(itemPickUp == null || itemPickUp.getData().getItemType().getMaxStackSize() == 64 || itemPickUp.getType().equals(Material.AIR))
            return;
        Inventory inv = null;
        Inventory topInv = player.getOpenInventory().getTopInventory();
        Inventory bottomInv = player.getOpenInventory().getBottomInventory();
        int slot = event.getSlot();
        if(!(bottomInv instanceof PlayerInventory) || !(topInv instanceof CraftingInventory && topInv.getSize() == 5))
        {
            shiftClickSeperateInv(itemPickUp, player, event, inv, topInv, bottomInv, slot);
        }
        else
        {
            shiftClickSameInv(itemPickUp, event, bottomInv);
        }
        player.updateInventory();
        event.setCancelled(true);
    }

    private static void shiftClickSeperateInv(ItemStack itemPickUp, Player player, InventoryClickEvent event, Inventory inv, Inventory topInv, Inventory bottomInv, int slot)
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
        if(inv instanceof PlayerInventory)
        {
            endSlot -= 5;

            if(topInv instanceof CraftingInventory && topInv.getSize() == 10)
            {
                playerOrder = true;
            }
        }
        else if(inv instanceof CraftingInventory)
        {
            ++startSlot;
        }
        else if(inv instanceof FurnaceInventory && itemPickUp.getType().isFuel())
        {
            --endSlot;
            reverse = true;
        }
        else if(inv instanceof EnchantingInventory)
        {
            --endSlot;
            if(inv.getItem(0) != null) return;
            ItemStack itemToMove = itemPickUp.clone();
            itemToMove.setAmount(1);
            StackUtils.moveItem(itemToMove, clickedInventory, slot, inv, startSlot, endSlot, reverse);
            itemPickUp.setAmount(itemPickUp.getAmount()-1);
            clickedInventory.setItem(slot, itemPickUp);
            return;
        }
        if(playerOrder)
        {
            StackUtils.moveItemPlayerOrder(itemPickUp, clickedInventory, slot, bottomInv);
        }
        else
        {
            StackUtils.moveItem(itemPickUp, clickedInventory, slot, inv, startSlot, endSlot, reverse);
        }
    }


    private static void shiftClickSameInv(ItemStack itemPickUp, InventoryClickEvent event, Inventory bottomInv)
    {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory inv;
        int slot = event.getSlot();
        inv = event.getClickedInventory();
        String type = itemPickUp.getType().toString();
        if(inv instanceof CraftingInventory)
        {
            StackUtils.moveItemPlayerOrder(itemPickUp, clickedInventory, slot, bottomInv);
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
                StackUtils.moveItem(itemPickUp, clickedInventory, slot, inv, 9, 36, false);
            }
            else if(slot < 36)
            {
                StackUtils.moveItem(itemPickUp, clickedInventory, slot, inv, 0, 9, false);
            }
            else
            {
                StackUtils.moveItem(itemPickUp, clickedInventory, slot, inv, 9, 36, false);
            }
        }
        else
        {
            if(slot < 36)
            {
                if(type.endsWith("_BOOTS"))
                {
                    inv.setItem(36, itemPickUp);
                    inv.setItem(slot, null);
                }
                else if(type.endsWith("_LEGGINGS"))
                {
                    inv.setItem(37, itemPickUp);
                    inv.setItem(slot, null);
                }
                else if(type.endsWith("_CHESTPLATE") || type.equals("ELYTRA"))
                {
                    inv.setItem(38, itemPickUp);
                    inv.setItem(slot, null);
                }
                else if(type.endsWith("_HELMET"))
                {
                    inv.setItem(39, itemPickUp);
                    inv.setItem(slot, null);
                }
                else if(type.equals("SHIELD"))
                {
                    inv.setItem(40, itemPickUp);
                    inv.setItem(slot, null);
                }
            }
            else
            {
                StackUtils.moveItem(itemPickUp, clickedInventory, slot, inv, 9, 36, false);
            }
        }
    }

    public static void pickupAll(Player player, ItemStack itemPickUp, InventoryView inventoryView, int rawSlot)
    {
        player.setItemOnCursor(itemPickUp);
        inventoryView.setItem(rawSlot, null);
    }

    public static void placeAll(Player player, ItemStack itemPickUp, ItemStack itemPutDown, InventoryView inventoryView, int rawSlot)
    {
        int newAmount = itemPickUp.getAmount() + itemPutDown.getAmount();
        int extraAmount = 0;
        if(newAmount > 64)
        {
            extraAmount = newAmount % 64;
            newAmount = 64;
        }
        itemPutDown.setAmount(newAmount);
        itemPickUp.setAmount(extraAmount);
        inventoryView.setItem(rawSlot, itemPutDown);
        player.setItemOnCursor(itemPickUp);
    }

    public static void pickupHalf(Player player, ItemStack itemPickUp, InventoryView inventoryView, int rawSlot)
    {
        ItemStack itemPutDown;
        int totalAmount = itemPickUp.getAmount();
        itemPickUp.setAmount((int) Math.ceil(totalAmount/2.0));
        itemPutDown = itemPickUp.clone();
        itemPutDown.setAmount((int) Math.floor(totalAmount/2.0));
        inventoryView.setItem(rawSlot, itemPutDown);
        player.setItemOnCursor(itemPickUp);
    }

    public static void cloneStack(Player player, ItemStack itemPickUp)
    {
        ItemStack itemPutDown;
        itemPutDown = itemPickUp.clone();
        itemPutDown.setAmount(64);
        player.setItemOnCursor(itemPutDown);
    }
}
