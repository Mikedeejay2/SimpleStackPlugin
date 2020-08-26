package com.mikedeejay2.simplestack.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InventoryDragListener implements Listener
{
//    @EventHandler
//    public void inventoryDragEvent(InventoryDragEvent event)
//    {
//        if(!event.getType().equals(DragType.EVEN)) return;
//        Inventory inventory = event.getInventory();
//        InventoryView inventoryView = event.getView();
//        Inventory topInv = inventoryView.getTopInventory();
//        Inventory bottomInv = inventoryView.getBottomInventory();
//        if(inventory.equals(topInv))
//        {
//            Bukkit.getConsoleSender().sendMessage("Top inv");
//            inventory = bottomInv;
//        }
//        else
//        {
//            Bukkit.getConsoleSender().sendMessage("Bottom inv");
//            inventory = topInv;
//        }
//        ItemStack cursor = event.getOldCursor();
//        Integer[] slots = event.getNewItems().keySet().toArray(new Integer[0]);
//        ItemStack[] newItems = event.getNewItems().values().toArray(new ItemStack[0]);
//        int amountOfItems = newItems.length;
//        int cursorSize = cursor.getAmount();
//        double amountPerItemRaw = (double)cursorSize/(double)amountOfItems;
//        int amountPerItem = (int) Math.floor(amountPerItemRaw);
//        int totalAmount = amountPerItem*amountOfItems;
//        int amountLeft = cursorSize-totalAmount;
//        Bukkit.getConsoleSender().sendMessage("amountOfItems: " + amountOfItems);
//        Bukkit.getConsoleSender().sendMessage("cursorSize: " + cursorSize);
//        Bukkit.getConsoleSender().sendMessage("amountPerItemRaw: " + amountPerItemRaw);
//        Bukkit.getConsoleSender().sendMessage("amountPerItem: " + amountPerItem);
//        Bukkit.getConsoleSender().sendMessage("totalAmount: " + totalAmount);
//        Bukkit.getConsoleSender().sendMessage("amountLeft: " + amountLeft);
//
//        ItemStack newCursor = cursor.clone();
//        newCursor.setAmount(amountLeft);
//        Player player = (Player) inventoryView.getPlayer();
//        player.setItemOnCursor(newCursor);
//        player.openInventory(inventoryView);
//
//        for(int i = 0; i < newItems.length; i++)
//        {
//            ItemStack item = newItems[i];
//            item.setAmount(amountPerItem);
//            inventory.setItem(slots[i], item);
//        }
//        player.updateInventory();
//        event.setCancelled(true);
//    }
}
