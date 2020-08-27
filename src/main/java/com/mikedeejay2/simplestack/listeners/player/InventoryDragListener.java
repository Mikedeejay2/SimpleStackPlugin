package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.simplestack.Simplestack;
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
        Integer[] slots = event.getNewItems().keySet().toArray(new Integer[0]);
        ItemStack[] newItems = event.getNewItems().values().toArray(new ItemStack[0]);
        int amountOfItems = newItems.length;
        int cursorSize = cursor.getAmount();
        double amountPerItemRaw = (double)cursorSize/(double)amountOfItems;
        int amountPerItem = (int) Math.floor(amountPerItemRaw);
        int totalAmount = amountPerItem*amountOfItems;
        int amountLeft = cursorSize-totalAmount;

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                ItemStack newCursor = cursor.clone();
                newCursor.setAmount(amountLeft);
                player.setItemOnCursor(newCursor);
            }
        }.runTask(plugin);

        for(int i = 0; i < newItems.length; i++)
        {
            ItemStack item = newItems[i];
            item.setAmount(amountPerItem);
            inventoryView.setItem(slots[i], item);
        }
        player.updateInventory();
        event.setCancelled(true);
    }
}
