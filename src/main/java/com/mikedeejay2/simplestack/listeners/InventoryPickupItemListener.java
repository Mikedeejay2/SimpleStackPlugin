package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.CancelUtils;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryPickupItemListener implements Listener
{
    private final Simplestack plugin;

    public InventoryPickupItemListener(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * This patches hoppers not properly picking up unstackable items and stacking them together in
     * inventories.
     *
     * @param event The event being activated
     */
    @EventHandler
    public void inventoryPickupItemEvent(InventoryPickupItemEvent event)
    {
        if(!plugin.config().shouldProcessHoppers()) return;
        Item item = event.getItem();
        ItemStack stack = item.getItemStack();

        boolean cancel = CancelUtils.cancelStackCheck(plugin, stack);
        if(cancel) return;
        event.setCancelled(true);

        Inventory inv = event.getInventory();

        MoveUtils.moveItemToInventory(plugin, event, item, inv, stack);
    }
}
