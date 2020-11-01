package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;

public class InventoryClickListener implements Listener
{
    private final Simplestack plugin;

    public InventoryClickListener(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Handles clicking for any item that doesn't normally stack to 64.
     * This is what lets players combine items into a stack in their inventory.
     *
     * @param event The event being activated
     */
    @EventHandler
    public void stackEvent(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        InventoryAction action = event.getAction();
        plugin.checkUtils().updateGUIManual(player.getOpenInventory().getTopInventory());
        if(plugin.cancelUtils().cancelPlayerCheck(player)) return;
        ItemStack itemPickUp = event.getCurrentItem();
        ItemStack itemPutDown = event.getCursor();
        ClickType clickType = event.getClick();
        InventoryView view = player.getOpenInventory();
        Inventory topInv = view.getTopInventory();
        Inventory bottomInv = view.getBottomInventory();
        int slot = event.getSlot();
        Inventory clickedInv = event.getClickedInventory();
        if(itemPickUp == null || action.toString().contains("DROP") || clickType == ClickType.CREATIVE) return;

        boolean cancel1 = plugin.cancelUtils().cancelStackCheck(itemPickUp);
        boolean cancel2 = plugin.cancelUtils().cancelStackCheck(itemPutDown);
        boolean cancel3 = plugin.cancelUtils().cancelGUICheck(topInv);
        if((cancel1 && cancel2) || event.isCancelled() || cancel3)
        {
            return;
        }
        event.setCancelled(true);

        plugin.checkUtils().useGUICheck(player, topInv, slot, clickedInv, clickType);

        if(action == InventoryAction.CLONE_STACK)
        {
            plugin.clickUtils().cloneStack(player, itemPickUp);
        }
        else if(action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.HOTBAR_MOVE_AND_READD)
        {
            event.setCancelled(false);
            return;
        }
        switch(clickType)
        {
            case LEFT:
                plugin.clickUtils().leftClick(itemPickUp, itemPutDown, player, event);
                break;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                plugin.clickUtils().shiftClick(itemPickUp, player, event);
                break;
            case RIGHT:
                plugin.clickUtils().rightClick(itemPickUp, itemPutDown, player, event);
                break;
        }
    }
}
