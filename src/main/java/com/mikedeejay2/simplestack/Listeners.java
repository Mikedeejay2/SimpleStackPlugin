package com.mikedeejay2.simplestack;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener
{
    // Plugin instance for referencing
    private static final Simplestack plugin = Simplestack.getInstance();

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

        StackUtils.makeUnique(itemPickUp, key);

        switch(clickType)
        {
            case LEFT:
                StackUtils.leftClick(itemPickUp, itemPutDown, player, event);
                break;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                StackUtils.shiftClick(itemPickUp, player, event);
                break;
            case RIGHT:
                StackUtils.rightClick(itemPickUp, itemPutDown, player, event);
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
        StackUtils.moveItemToInventory(event, event.getItem(), player, item);
    }
}
