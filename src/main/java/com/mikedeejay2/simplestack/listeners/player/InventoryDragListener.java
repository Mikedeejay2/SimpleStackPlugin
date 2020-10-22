package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.BeaconInventory;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InventoryDragListener implements Listener
{
    private final Simplestack plugin;

    public InventoryDragListener(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * This method properly distributes un-stackable items that have been stacked evenly
     * across the inventory slots. This fixes the 1 per item bug that was happening
     * before this was implemented.
     *
     * @param event The event being activated
     */
    @EventHandler
    public void inventoryDragEvent(InventoryDragEvent event)
    {
        if(event.getType() != DragType.EVEN) return;
        InventoryView inventoryView = event.getView();
        if(event.getInventory() instanceof BrewerInventory || event.getInventory() instanceof BeaconInventory) return;
        Player player = (Player) inventoryView.getPlayer();
        if(plugin.cancelUtils().cancelPlayerCheck(player)) return;

        ItemStack cursor = event.getOldCursor();
        if(plugin.cancelUtils().cancelStackCheck(cursor.getType())) return;
        GameMode gameMode = player.getGameMode();
        if(gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE)
        {
            plugin.moveUtils().dragItemsSurvival(event, inventoryView, player, cursor);
        }
        else
        {
            plugin.moveUtils().dragItemsCreative(event, inventoryView, player, cursor);
        }

        player.updateInventory();
        event.setCancelled(true);
    }
}
