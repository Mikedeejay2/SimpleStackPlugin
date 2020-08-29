package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.CancelUtils;
import com.mikedeejay2.simplestack.util.CheckUtils;
import com.mikedeejay2.simplestack.util.ClickUtils;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;

public class InventoryClickListener implements Listener
{
    // Plugin instance for referencing
    private static final Simplestack plugin = Simplestack.getInstance();

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
        CheckUtils.updateGUIManual(player.getOpenInventory().getTopInventory());
        if(CancelUtils.cancelPlayerCheck(player)) return;
        ItemStack itemPickUp = event.getCurrentItem();
        ItemStack itemPutDown = event.getCursor();
        ClickType clickType = event.getClick();
        InventoryView view = player.getOpenInventory();
        Inventory topInv = view.getTopInventory();
        Inventory bottomInv = view.getBottomInventory();
        int slot = event.getSlot();
        Inventory clickedInv = event.getClickedInventory();
        if(itemPickUp == null || action.toString().contains("DROP") || clickType == ClickType.CREATIVE) return;

        boolean cancel1 = CancelUtils.cancelStackCheck(itemPickUp.getType());
        boolean cancel2 = CancelUtils.cancelStackCheck(itemPutDown.getType());
        if((cancel1 && cancel2) || event.isCancelled())
        {
            return;
        }
        event.setCancelled(true);

        StackUtils.removeUnique(itemPickUp, plugin.getKey());

        CheckUtils.useGUICheck(player, topInv, slot, clickedInv, clickType);

        if(action == InventoryAction.CLONE_STACK)
        {
            ClickUtils.cloneStack(player, itemPickUp);
        }
        else if(action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.HOTBAR_MOVE_AND_READD)
        {
            event.setCancelled(false);
            return;
        }
        switch(clickType)
        {
            case LEFT:
                ClickUtils.leftClick(itemPickUp, itemPutDown, player, event);
                break;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                ClickUtils.shiftClick(itemPickUp, player, event);
                break;
            case RIGHT:
                ClickUtils.rightClick(itemPickUp, itemPutDown, player, event);
                break;
        }
    }
}
