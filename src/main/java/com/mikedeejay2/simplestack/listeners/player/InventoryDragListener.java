package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.ClickUtils;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryDragListener implements Listener
{
    private static final Simplestack plugin = Simplestack.getInstance();

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
        if(!event.getType().equals(DragType.EVEN)) return;
        InventoryView inventoryView = event.getView();
        Player player = (Player) inventoryView.getPlayer();
        if(StackUtils.cancelPlayerCheck(player)) return;

        ItemStack cursor = event.getOldCursor();
        if(StackUtils.cancelStackCheck(cursor.getType())) return;
        ClickUtils.dragItems(event, inventoryView, player, cursor);

        player.updateInventory();
        event.setCancelled(true);
    }
}
