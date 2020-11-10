package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.CancelUtils;
import com.mikedeejay2.simplestack.util.MoveUtils;
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
        if(!plugin.config().isHopperMovement()) return;
        ItemStack item = event.getItem();

        boolean cancel = CancelUtils.cancelStackCheck(plugin, item);
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
                MoveUtils.moveItemToInventory(plugin, item, fromInv, toInv, amountBeingMoved);
            }
        }.runTaskLater(plugin, 0);
    }
}
