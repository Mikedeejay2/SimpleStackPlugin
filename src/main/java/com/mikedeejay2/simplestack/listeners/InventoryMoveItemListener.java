package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryMoveItemListener implements Listener
{
    private final Simplestack plugin;

    public InventoryMoveItemListener(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * This patches hoppers not properly stacking unstackable items together in
     * inventories.
     *
     * @param event The event being activated
     */
    @EventHandler
    public void inventoryMoveItemEvent(InventoryMoveItemEvent event)
    {
        ItemStack item = event.getItem();

        boolean cancel = plugin.cancelUtils().cancelStackCheck(item);
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
                plugin.moveUtils().moveItemToInventory(item, fromInv, toInv, amountBeingMoved);
            }
        }.runTaskLater(plugin, 0);
    }
}
