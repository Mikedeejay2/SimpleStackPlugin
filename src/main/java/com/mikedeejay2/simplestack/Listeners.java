package com.mikedeejay2.simplestack;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Listeners implements Listener
{
    private Simplestack plugin = Simplestack.getInstance();

    @EventHandler
    public void stackEvent(InventoryClickEvent event)
    {
        ItemStack itemPickUp = event.getCurrentItem();
        ItemStack itemPutDown = event.getCursor();
        Player player = (Player) event.getWhoClicked();

        NamespacedKey key = new NamespacedKey(plugin, "simplestack");

        if(event.getClick().isLeftClick())
        {
            normalClick(itemPickUp, itemPutDown, key, event);
        }
    }

    private void normalClick(ItemStack itemPickUp, ItemStack itemPutDown, NamespacedKey key, InventoryClickEvent event)
    {
        if(itemPickUp != null && itemPickUp.getData().getItemType().getMaxStackSize() != 64 && !itemPickUp.getType().equals(Material.AIR))
        {
            ItemMeta itemMeta = itemPickUp.getItemMeta();
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            if(!data.has(key, PersistentDataType.BYTE))
            {
                data.set(key, PersistentDataType.BYTE, (byte) 1);
                itemPickUp.setItemMeta(itemMeta);
            }

            if(itemPutDown != null && itemPutDown.getData().getItemType().getMaxStackSize() != 64 && !itemPutDown.getType().equals(Material.AIR))
            {
                if(itemPickUp.getAmount() == 64 || itemPutDown.getAmount() == 64) event.setCancelled(true);
                if(equalsEachOther(itemPutDown, itemPickUp))
                {
                    int newAmount = itemPutDown.getAmount() + itemPickUp.getAmount();
                    int extraAmount = 0;
                    if(newAmount > 64)
                    {
                        extraAmount = (newAmount - 64);
                        newAmount = 64;
                    }
                    itemPutDown.setAmount(newAmount);
                    itemPickUp.setAmount(extraAmount);
                }
            }
        }
    }

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
