package com.mikedeejay2.simplestack;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Listeners implements Listener
{
    private Simplestack plugin = Simplestack.getInstance();

    public static final int MAX_AMOUNT_IN_STACK = 64;

    @EventHandler
    public void stackEvent(InventoryClickEvent event)
    {
        ItemStack itemPickUp = event.getCurrentItem();
        ItemStack itemPutDown = event.getCursor();
        Player player = (Player) event.getWhoClicked();

        NamespacedKey key = new NamespacedKey(plugin, "simplestack");

        if(itemPickUp != null && itemPickUp.getData().getItemType().getMaxStackSize() != 64 && !itemPickUp.getType().equals(Material.AIR))
        {
            makeUnique(itemPickUp, key);

            if(event.getClick().equals(ClickType.LEFT))
            {
                leftClick(itemPickUp, itemPutDown, player, event);
            }
            else if(event.getClick().equals(ClickType.SHIFT_LEFT) || event.getClick().equals(ClickType.SHIFT_RIGHT))
            {
                shiftClick(itemPickUp, player, event);
            }
            else if(event.getClick().equals(ClickType.RIGHT))
            {
                rightClick(itemPickUp, itemPutDown, player, event);
            }
        }
    }

    @EventHandler
    public void entityPickupItemEvent(EntityPickupItemEvent event)
    {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        ItemStack item = event.getItem().getItemStack();
        moveItemToInventory(event, event.getItem(), player, item);
    }

    private void moveItemToInventory(Cancellable event, Item groundItem, Player player, ItemStack item)
    {
        if(item.getType().getMaxStackSize() == 64) return;
        PlayerInventory inv = player.getInventory();
        for(int i = 0; i < inv.getSize(); i++)
        {
            if(moveItemInternal(item, inv, i))
            {
                groundItem.remove();
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.1f, 1);
                event.setCancelled(true);
                break;
            }
        }
        if(item.getAmount() != 0)
        {
            for(int i = 0; i < inv.getSize(); i++)
            {
                if(inv.getItem(i) == null)
                {
                    inv.setItem(i, item);
                    groundItem.remove();
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.1f, 1);
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    private void leftClick(ItemStack itemPickUp, ItemStack itemPutDown, Player player, InventoryClickEvent event)
    {
        if(itemPutDown != null && itemPutDown.getData().getItemType().getMaxStackSize() != 64 && !itemPutDown.getType().equals(Material.AIR))
        {
            if(equalsEachOther(itemPutDown, itemPickUp))
            {
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
        }
    }

    private void rightClick(ItemStack itemPickUp, ItemStack itemPutDown, Player player, InventoryClickEvent event)
    {
        if(itemPutDown != null && itemPutDown.getData().getItemType().getMaxStackSize() != 64 && !itemPutDown.getType().equals(Material.AIR))
        {
            if(equalsEachOther(itemPutDown, itemPickUp))
            {
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
        }
    }

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

                moveItem(itemPickUp, player, event, inv, 0, inv.getSize(), false);
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
                        moveItem(itemPickUp, player, event, inv, 9, 36, false);
                    }
                    else if(event.getSlot() < 36)
                    {
                        moveItem(itemPickUp, player, event, inv, 0, 8, false);
                    }
                    else
                    {
                        moveItem(itemPickUp, player, event, inv, 9, 36, false);
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
                        moveItem(itemPickUp, player, event, event.getClickedInventory(), 9, 36, false);
                    }
                }
            }
            player.updateInventory();
            event.setCancelled(true);
        }
    }

    private void moveItem(ItemStack itemPickUp, Player player, InventoryClickEvent event, Inventory inv, int startingSlot, int endingSlot, boolean reverse)
    {
        if(!reverse)
        {
            for(int i = startingSlot; i < endingSlot; i++)
            {
                if(moveItemInternal(itemPickUp, inv, i)) break;
            }
            if(itemPickUp.getAmount() != 0)
            {
                for(int i = startingSlot; i < endingSlot; i++)
                {
                    if(inv.getItem(i) == null)
                    {
                        inv.setItem(i, itemPickUp);
                        event.getClickedInventory().setItem(event.getSlot(), null);
                        break;
                    }
                }
            }
        }
        else
        {
            for(int i = endingSlot-1; i >= startingSlot; i--)
            {
                if(moveItemInternal(itemPickUp, inv, i)) break;
            }
            if(itemPickUp.getAmount() != 0)
            {
                for(int i = endingSlot-1; i >= startingSlot; i--)
                {
                    if(inv.getItem(i) == null)
                    {
                        inv.setItem(i, itemPickUp);
                        event.getClickedInventory().setItem(event.getSlot(), null);
                        break;
                    }
                }
            }
        }
    }

    private boolean moveItemInternal(ItemStack itemPickUp, Inventory inv, int i)
    {
        ItemStack itemStack = inv.getItem(i);
        if(itemStack != null && equalsEachOther(itemPickUp, itemStack))
        {
            int newAmount = itemStack.getAmount() + itemPickUp.getAmount();
            int extraAmount = 0;
            if(newAmount > MAX_AMOUNT_IN_STACK)
            {
                extraAmount = (newAmount - MAX_AMOUNT_IN_STACK);
                newAmount = MAX_AMOUNT_IN_STACK;
            }
            itemStack.setAmount(newAmount);
            itemPickUp.setAmount(extraAmount);
            if(itemPickUp.getAmount() == 0)
            {
                return true;
            }
        }
        return false;
    }

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

    // We can't use .equals() because it also checks the amount variable which shouldn't be checked in this case
    private boolean equalsEachOther(ItemStack stack1, ItemStack stack2)
    {
        ItemMeta meta1 = stack1.getItemMeta();
        ItemMeta meta2 = stack2.getItemMeta();
        return meta1.equals(meta2);
    }
}
