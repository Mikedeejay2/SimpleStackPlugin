package com.mikedeejay2.simplestack;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;

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
            ItemMeta itemMeta = itemPickUp.getItemMeta();
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            if(!data.has(key, PersistentDataType.BYTE))
            {
                data.set(key, PersistentDataType.BYTE, (byte) 1);
                itemPickUp.setItemMeta(itemMeta);
            }

            if(event.getClick().equals(ClickType.LEFT))
            {
                normalClick(itemPickUp, itemPutDown, player, event);
            }
            else if(event.getClick().equals(ClickType.SHIFT_LEFT) || event.getClick().equals(ClickType.SHIFT_RIGHT))
            {
                shiftClick(itemPickUp, player, event);
            }
        }
    }

    private void normalClick(ItemStack itemPickUp, ItemStack itemPutDown, Player player, InventoryClickEvent event)
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

    private void shiftClick(ItemStack itemPickUp, Player player, InventoryClickEvent event)
    {
        if(itemPickUp != null && itemPickUp.getData().getItemType().getMaxStackSize() != 64 && !itemPickUp.getType().equals(Material.AIR))
        {
            Inventory inv = null;
            if(event.getClickedInventory().equals(player.getOpenInventory().getBottomInventory()))
            {
                inv = player.getOpenInventory().getTopInventory();
            }
            else if(event.getClickedInventory().equals(player.getOpenInventory().getTopInventory()))
            {
                inv = player.getOpenInventory().getBottomInventory();
            }

            for(int i = 0; i < inv.getSize(); i++)
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
                        break;
                    }
                }
            }
            if(itemPickUp.getAmount() != 0)
            {
                HashMap<Integer, ItemStack> temp = inv.addItem(itemPickUp);
                if(temp.isEmpty())
                {
                    event.getClickedInventory().setItem(event.getSlot(), null);
                }
            }
            player.updateInventory();
            event.setCancelled(true);
        }
    }

    // We can't use .equals() because it also checks the amount variable which shouldn't be checked in this case
    private boolean equalsEachOther(ItemStack stack1, ItemStack stack2)
    {
        ItemMeta meta1 = stack1.getItemMeta();
        ItemMeta meta2 = stack2.getItemMeta();
        if(!stack1.getType().equals(stack2.getType())) return false;
        if(meta1.hasAttributeModifiers() != meta2.hasAttributeModifiers()) return false;
        if(meta1.hasCustomModelData() != meta2.hasCustomModelData()) return false;
        if(meta1.hasDisplayName() != meta2.hasDisplayName()) return false;
        if(meta1.hasEnchants() != meta2.hasEnchants())return false;
        if(meta1.hasLore() != meta2.hasLore()) return false;
        if(meta1.hasDisplayName() && meta2.hasDisplayName()) if(!meta1.getDisplayName().equals(meta2.getDisplayName())) return false;
        if(meta1.hasLore() && meta2.hasLore()) if(!meta1.getLore().equals(meta2.getLore())) return false;
        if(meta1.hasEnchants() && meta2.hasEnchants()) if(!meta1.getEnchants().equals(meta2.getEnchants())) return false;
        if(meta1.hasCustomModelData() && meta2.hasCustomModelData()) if(meta1.getCustomModelData() != meta2.getCustomModelData()) return false;
        if(meta1.hasAttributeModifiers() && meta2.hasAttributeModifiers()) if(!meta1.getAttributeModifiers().equals(meta2.getAttributeModifiers())) return false;
        if(!meta1.getPersistentDataContainer().equals(meta2.getPersistentDataContainer())) return false;
        return true;
    }
}
