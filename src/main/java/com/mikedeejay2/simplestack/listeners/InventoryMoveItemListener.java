package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.CancelUtils;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
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
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void inventoryMoveItemEvent(InventoryMoveItemEvent event)
    {
        if(!plugin.config().shouldProcessHoppers()) return;
        ItemStack     item    = event.getItem();
        Inventory     fromInv = event.getSource();
        Inventory     toInv   = event.getDestination();
        InventoryType invType = toInv.getType();
        if(invType == InventoryType.BREWING) return;
        if(toInv.getLocation() != null && InventoryIdentifiers.isShulkerBox(item.getType()))
        {
            Location location  = toInv.getLocation();
            World    world     = location.getWorld();
            Block    block     = world.getBlockAt(location);
            Material blockType = block.getType();
            if(InventoryIdentifiers.isShulkerBox(blockType)) return;
        }

        boolean cancel = CancelUtils.cancelStackCheck(plugin, item);
        if(cancel) return;
        event.setCancelled(true);

        int amountBeingMoved = item.getAmount();


        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                MoveUtils.moveItemToInventory(plugin, item, fromInv, toInv, amountBeingMoved);
            }
        }.runTask(plugin);
    }
}
