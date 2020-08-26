package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.StackUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryMoveItemListener implements Listener
{
    // Plugin instance for referencing
    private static final Simplestack plugin = Simplestack.getInstance();

    @EventHandler
    public void inventoryMoveItemEvent(InventoryMoveItemEvent event)
    {
        ItemStack item = event.getItem();

        boolean cancel = StackUtils.cancelStackCheck(item.getType());
        if(cancel) return;
        event.setCancelled(true);

        Inventory fromInv = event.getSource();
        Inventory toInv = event.getDestination();
        int amountBeingMoved = item.getAmount();

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                StackUtils.moveItemToInventory(item, fromInv, toInv, amountBeingMoved);
            }
        }.runTaskLater(plugin, 0);

    }
}
