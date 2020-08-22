package com.mikedeejay2.simplestack;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/*
 * This is where all of the code for this plugin exists.
 * I could split the code into other classes but since there is
 * so little code required to make this idea work I thought I would
 * just leave it in here for now.
 */
public class Listeners implements Listener
{
    // Plugin instance for referencing
    private static final Simplestack plugin = Simplestack.getInstance();

    // Max stack size. Changing this produces some really weird results because
    // Minecraft really doesn't know how to handle anything higher than 64.
    public static final int MAX_AMOUNT_IN_STACK = 64;

    // A namespaced key for adding a small piece of NBT data that makes each item "Unique".
    // This has to happen because if we don't make each item unique then the InventoryClickEvent won't be called
    // when trying to stack 2 fully stacked items of the same type.
    // Certainly a hacky work around, but it works.
    private static final NamespacedKey key = new NamespacedKey(plugin, "simplestack");

    /*
     * InventoryClickEvent handler,
     * This does a majority of the work for this plugin.
     */
    @EventHandler
    public void stackEvent(InventoryClickEvent event)
    {
        ItemStack itemPickUp = event.getCurrentItem();
        ItemStack itemPutDown = event.getCursor();
        Player player = (Player) event.getWhoClicked();
        ClickType clickType = event.getClick();

        if(itemPickUp == null ||
        itemPickUp.getData().getItemType().getMaxStackSize() == 64 ||
        itemPickUp.getType().equals(Material.AIR))
            return;

        makeUnique(itemPickUp, key);

        switch(clickType)
        {
            case LEFT:
                leftClick(itemPickUp, itemPutDown, player, event);
                break;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                shiftClick(itemPickUp, player, event);
                break;
            case RIGHT:
                rightClick(itemPickUp, itemPutDown, player, event);
                break;
        }
    }

    /*
     * EntityPickupItemEvent
     * This is for when multiple unstackable items are on the ground and
     * are picked up by a player.
     * This code will automatically stack them in their inventory as if they
     * were a stack of 64.
     */
    @EventHandler
    public void entityPickupItemEvent(EntityPickupItemEvent event)
    {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        ItemStack item = event.getItem().getItemStack();
        moveItemToInventory(event, event.getItem(), player, item);
    }

    /*
     * This helper method takes an item that is on the ground and moves
     * it into a player's inventory while also attempting to stack the
     * item with other items in the player's inventory.
     */
    private void moveItemToInventory(Cancellable event, Item groundItem, Player player, ItemStack item)
    {
        if(item.getType().getMaxStackSize() == 64) return;
        PlayerInventory inv = player.getInventory();
        for(int i = 0; i < inv.getSize(); i++)
        {
            if(!moveItemInternal(item, inv, i)) continue;
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

    /*
     * This helped method is a left click event that attempts to
     * stack 2 items together to a stack of 64.
     */
    private void leftClick(ItemStack itemPickUp, ItemStack itemPutDown, Player player, InventoryClickEvent event)
    {
        if(itemPutDown == null ||
        itemPutDown.getData().getItemType().getMaxStackSize() == 64 &&
        itemPutDown.getType().equals(Material.AIR))
            return;
        if(!equalsEachOther(itemPutDown, itemPickUp)) return;
        int newAmount = itemPutDown.getAmount() + itemPickUp.getAmount();
        int extraAmount = 0;
        if(newAmount > MAX_AMOUNT_IN_STACK)
        {
            extraAmount = (newAmount - MAX_AMOUNT_IN_STACK);
            newAmount = MAX_AMOUNT_IN_STACK;
        }
        itemPutDown.setAmount(newAmount);
        itemPickUp.setAmount(extraAmount);
        event.getClickedInventory().setItem(event.getSlot(), itemPutDown);
        player.getOpenInventory().setCursor(itemPickUp);
        player.updateInventory();
        event.setCancelled(true);
    }

    /*
     * This helper method attempts to emulate a right click event where
     * one item is removed from the held stack and put down to the slot
     * being right clicked.
     */
    private void rightClick(ItemStack itemPickUp, ItemStack itemPutDown, Player player, InventoryClickEvent event)
    {
        if(itemPutDown == null ||
        itemPutDown.getData().getItemType().getMaxStackSize() == 64 ||
        itemPutDown.getType().equals(Material.AIR))
            return;
        if(!equalsEachOther(itemPutDown, itemPickUp)) return;
        if(itemPutDown.getAmount() > 0)
        {
            int bottomAmount = itemPickUp.getAmount() + 1;
            int topAmount = itemPutDown.getAmount() - 1;
            itemPickUp.setAmount(bottomAmount);
            itemPutDown.setAmount(topAmount);
        }
        player.updateInventory();
        event.setCancelled(true);
    }

    /*
     * This helper method emulates the action of shift clicking an item into another inventory.
     * This if the biggest method because there's a lot that has a possibility
     * of happening when shift clicking.
     */
    private void shiftClick(ItemStack itemPickUp, Player player, InventoryClickEvent event)
    {
        if(itemPickUp != null && itemPickUp.getData().getItemType().getMaxStackSize() != 64 && !itemPickUp.getType().equals(Material.AIR))
        {
            Inventory inv = null;
            if(!(player.getOpenInventory().getBottomInventory() instanceof PlayerInventory && player.getOpenInventory().getTopInventory() instanceof CraftingInventory))
            {
                if(event.getClickedInventory().equals(player.getOpenInventory().getBottomInventory()))
                {
                    inv = player.getOpenInventory().getTopInventory();
                }
                else if(event.getClickedInventory().equals(player.getOpenInventory().getTopInventory()))
                {
                    inv = player.getOpenInventory().getBottomInventory();
                }

                moveItem(itemPickUp, event, inv, 0, inv.getSize()-5, false);
            }
            else
            {
                inv = event.getClickedInventory();
                String type = itemPickUp.getType().toString();
                if(!type.endsWith("_HELMET") &&
                   !type.endsWith("_CHESTPLATE") &&
                   !type.endsWith("_LEGGINGS") &&
                   !type.endsWith("_BOOTS") &&
                   !type.equals("SHIELD"))
                {
                    if(event.getSlot() < 9)
                    {
                        moveItem(itemPickUp, event, inv, 9, 36, false);
                    }
                    else if(event.getSlot() < 36)
                    {
                        moveItem(itemPickUp, event, inv, 0, 8, false);
                    }
                    else
                    {
                        moveItem(itemPickUp, event, inv, 9, 36, false);
                    }
                }
                else
                {
                    if(event.getSlot() < 36)
                    {
                        if(type.endsWith("_BOOTS"))
                        {
                            inv.setItem(36, itemPickUp);
                            inv.setItem(event.getSlot(), null);
                        }
                        else if(type.endsWith("_LEGGINGS"))
                        {
                            inv.setItem(37, itemPickUp);
                            inv.setItem(event.getSlot(), null);
                        }
                        else if(type.endsWith("_CHESTPLATE"))
                        {
                            inv.setItem(38, itemPickUp);
                            inv.setItem(event.getSlot(), null);
                        }
                        else if(type.endsWith("_HELMET"))
                        {
                            inv.setItem(39, itemPickUp);
                            inv.setItem(event.getSlot(), null);
                        }
                        else if(type.equals("SHIELD"))
                        {
                            inv.setItem(40, itemPickUp);
                            inv.setItem(event.getSlot(), null);
                        }
                    }
                    else
                    {
                        moveItem(itemPickUp, event, event.getClickedInventory(), 9, 36, false);
                    }
                }
            }
            player.updateInventory();
            event.setCancelled(true);
        }
    }

    /*
     * This helper method moves an item into a player's inventory with a set starting slot
     * and ending slot to search through. This method can also be called in reverse if needed.
     */
    private void moveItem(ItemStack itemPickUp, InventoryClickEvent event, Inventory inv, int startingSlot, int endingSlot, boolean reverse)
    {
        if(!reverse)
        {
            for(int i = startingSlot; i < endingSlot; i++)
            {
                if(moveItemInternal(itemPickUp, inv, i)) break;
            }
            if(itemPickUp.getAmount() == 0) return;
            for(int i = startingSlot; i < endingSlot; i++)
            {
                if(inv.getItem(i) != null) continue;
                inv.setItem(i, itemPickUp);
                event.getClickedInventory().setItem(event.getSlot(), null);
                break;
            }
        }
        else
        {
            for(int i = endingSlot-1; i >= startingSlot; i--)
            {
                if(moveItemInternal(itemPickUp, inv, i)) break;
            }
            if(itemPickUp.getAmount() == 0) return;
            for(int i = endingSlot-1; i >= startingSlot; i--)
            {
                if(inv.getItem(i) != null) continue;
                inv.setItem(i, itemPickUp);
                event.getClickedInventory().setItem(event.getSlot(), null);
                break;
            }
        }
    }

    /*
     * A helper method that attempts to move an item into a slot if all conditions
     * are correct (same type of item, not already stacked to 64, etc).
     */
    private boolean moveItemInternal(ItemStack itemPickUp, Inventory inv, int i)
    {
        ItemStack itemStack = inv.getItem(i);
        if(itemStack == null || !equalsEachOther(itemPickUp, itemStack)) return false;
        int newAmount = itemStack.getAmount() + itemPickUp.getAmount();
        int extraAmount = 0;
        if(newAmount > MAX_AMOUNT_IN_STACK)
        {
            extraAmount = (newAmount - MAX_AMOUNT_IN_STACK);
            newAmount = MAX_AMOUNT_IN_STACK;
        }
        itemStack.setAmount(newAmount);
        itemPickUp.setAmount(extraAmount);
        return itemPickUp.getAmount() == 0;
    }

    /*
     * This makes an item unique and fools the Minecraft client to sending a move item
     * packet even with "fully stacked" items. This has to be worked around because
     * without InventoryClickEvent on fully stacked items there would be no way
     * for this code to know if the player was trying to combine fully stacked
     * items.
     */
    private void makeUnique(ItemStack itemPickUp, NamespacedKey key)
    {
        ItemMeta itemMeta = itemPickUp.getItemMeta();
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        if(!data.has(key, PersistentDataType.BYTE))
        {
            data.set(key, PersistentDataType.BYTE, (byte) 1);
            itemPickUp.setItemMeta(itemMeta);
        }
    }

    /*
     * Simple helper method that takes 2 item metas
     * and checks to see if they equal each other.
     * Maybe the most important method in this code.
     */
    private boolean equalsEachOther(ItemStack stack1, ItemStack stack2)
    {
        ItemMeta meta1 = stack1.getItemMeta();
        ItemMeta meta2 = stack2.getItemMeta();
        if(meta1 == null || meta2 == null) return false;
        return meta1.equals(meta2);
    }
}
