package com.mikedeejay2.simplestack;

import com.mikedeejay2.simplestack.config.Config;
import com.mikedeejay2.simplestack.config.ListMode;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class StackUtils
{
    // Plugin instance for referencing
    private static final Simplestack plugin = Simplestack.getInstance();

    // Max stack size. Changing this produces some really weird results because
    // Minecraft really doesn't know how to handle anything higher than 64.
    public static final int MAX_AMOUNT_IN_STACK = 64;

    private static final NamespacedKey key = new NamespacedKey(plugin, "simplestack");

    /*
     * This helper method takes an item that is on the ground and moves
     * it into a player's inventory while also attempting to stack the
     * item with other items in the player's inventory.
     */
    public static void moveItemToInventory(Cancellable event, Item groundItem, Player player, ItemStack item)
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
            if(!moveItemInternal(item, inv, i)) continue;
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

    /*
     * This helped method is a left click event that attempts to
     * stack 2 items together to a stack of 64.
     */
    public static void leftClick(ItemStack itemPickUp, ItemStack itemPutDown, Player player, InventoryClickEvent event)
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
    public static void rightClick(ItemStack itemPickUp, ItemStack itemPutDown, Player player, InventoryClickEvent event)
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
    public static void shiftClick(ItemStack itemPickUp, Player player, InventoryClickEvent event)
    {
        if(itemPickUp != null && itemPickUp.getData().getItemType().getMaxStackSize() != 64 && !itemPickUp.getType().equals(Material.AIR))
        {
            Inventory inv = null;
            Inventory topInv = player.getOpenInventory().getTopInventory();
            Inventory bottomInv = player.getOpenInventory().getBottomInventory();
            if(!(player.getOpenInventory().getBottomInventory() instanceof PlayerInventory && player.getOpenInventory().getTopInventory() instanceof CraftingInventory))
            {
                if(event.getClickedInventory().equals(bottomInv))
                {
                    inv = player.getOpenInventory().getTopInventory();
                }
                else if(event.getClickedInventory().equals(topInv))
                {
                    inv = player.getOpenInventory().getBottomInventory();

                    if(topInv instanceof AnvilInventory && event.getSlot() == 2)
                    {
                        ItemStack item1 = topInv.getItem(0);
                        ItemStack item2 = topInv.getItem(1);
                        if(item1 != null) item1.setAmount(item1.getAmount()-1);
                        if(item2 != null) item2.setAmount(item2.getAmount()-1);
                        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                    }
                }

                moveItem(itemPickUp, event, inv, 0, inv instanceof PlayerInventory ? inv.getSize()-5 : inv.getSize(), false);
            }
            else
            {
                inv = event.getClickedInventory();
                String type = itemPickUp.getType().toString();
                if(!type.endsWith("_HELMET") &&
                        !type.endsWith("_CHESTPLATE") &&
                        !type.endsWith("_LEGGINGS") &&
                        !type.endsWith("_BOOTS") &&
                        !type.equals("SHIELD") &&
                        !type.equals("ELYTRA"))
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
                        else if(type.endsWith("_CHESTPLATE") || type.equals("ELYTRA"))
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
    public static void moveItem(ItemStack itemPickUp, InventoryClickEvent event, Inventory inv, int startingSlot, int endingSlot, boolean reverse)
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
    public static boolean moveItemInternal(ItemStack itemPickUp, Inventory inv, int i)
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
    public static void makeUnique(ItemStack itemPickUp, NamespacedKey key)
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
    public static boolean equalsEachOther(ItemStack stack1, ItemStack stack2)
    {
        ItemMeta meta1 = stack1.getItemMeta();
        ItemMeta meta2 = stack2.getItemMeta();
        if(meta1 == null || meta2 == null) return false;
        if(!meta1.equals(meta2)) return false;
        if(!stack1.getType().equals(stack2.getType())) return false;
        return true;
    }

    /*
     * If the player doesn't have the permission "simplestack.use" (enabled by default)
     * then this will tell the event it's being called in to return and not run stacking code.
     */
    public static boolean cancelStackCheck(Material material)
    {
        Config config = Simplestack.getCustomConfig();
        if(material == null ||
           material.getMaxStackSize() == 64 ||
           material.equals(Material.AIR))
            return true;
        if(Simplestack.getCustomConfig().LIST_MODE.equals(ListMode.BLACKLIST))
        {
            return config.LIST.contains(material);
        }
        else
        {
            return !config.LIST.contains(material);
        }
    }

    /*
     * By default shulker boxes unstack items when broken, this stops that.
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
}
