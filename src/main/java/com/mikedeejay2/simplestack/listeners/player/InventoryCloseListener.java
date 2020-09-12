package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.CancelUtils;
import com.mikedeejay2.simplestack.util.MoveUtils;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryCloseListener implements Listener
{
    // Plugin instance for referencing
    private static final Simplestack plugin = Simplestack.getInstance();

    /**
     * When a crafting table is closed, if it contains items it will be placed
     * back into the player's inventory without unstacking the items inside,
     * because Minecraft doesn't like what this plugin is trying to accomplish.
     *
     * @param event The event being activated
     */
    @EventHandler
    public void craftingTableCloseEvent(InventoryCloseEvent event)
    {
        Player player = (Player) event.getPlayer();
        if(CancelUtils.cancelPlayerCheck(player)) return;
        Inventory inv = event.getInventory();
        InventoryType type = inv.getType();
        if(!(type == InventoryType.WORKBENCH ||
                type == InventoryType.ENCHANTING ||
                type == InventoryType.ANVIL ||
                type == InventoryType.LOOM ||
                type == InventoryType.GRINDSTONE ||
                (plugin.getMCVersion()[1] >= 16 && type == InventoryType.SMITHING)
          ) ) return;
        Inventory playerInv = player.getInventory();
        MoveUtils.moveAllItemsToPlayerInv(inv, player, playerInv);

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                player.updateInventory();
            }
        }.runTask(plugin);
    }
}
