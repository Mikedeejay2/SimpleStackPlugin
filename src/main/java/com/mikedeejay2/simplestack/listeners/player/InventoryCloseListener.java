package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.StackUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class InventoryCloseListener implements Listener
{
    // Plugin instance for referencing
    private static final Simplestack plugin = Simplestack.getInstance();

    @EventHandler
    public void craftingTableCloseEvent(InventoryCloseEvent event)
    {
        Player player = (Player) event.getPlayer();
        if(StackUtils.cancelPlayerCheck(player)) return;
        Inventory inv = event.getInventory();
        InventoryType type = inv.getType();
        if(!(type.equals(InventoryType.WORKBENCH) ||
                type.equals(InventoryType.ENCHANTING) ||
                type.equals(InventoryType.ANVIL))) return;
        Inventory playerInv = player.getInventory();
        StackUtils.moveAllItemsToPlayerInv(inv, player, playerInv);
    }
}
