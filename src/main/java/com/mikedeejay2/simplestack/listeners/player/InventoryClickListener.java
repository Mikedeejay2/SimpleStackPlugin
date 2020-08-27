package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.ClickUtils;
import com.mikedeejay2.simplestack.util.StackUtils;
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
        if(StackUtils.cancelPlayerCheck(player)) return;
        StackUtils.updateAnvilManual(player.getOpenInventory().getTopInventory());
        ItemStack itemPickUp = event.getCurrentItem();
        ItemStack itemPutDown = event.getCursor();
        ClickType clickType = event.getClick();
        if(itemPickUp == null || action.toString().contains("DROP") || clickType.equals(ClickType.CREATIVE)) return;

        boolean cancel = StackUtils.cancelStackCheck(itemPickUp.getType());
        if(cancel || event.isCancelled()) return;
        event.setCancelled(true);

        StackUtils.makeUnique(itemPickUp, plugin.getKey());

        if(action.equals(InventoryAction.CLONE_STACK))
        {
            ClickUtils.cloneStack(player, itemPickUp);
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
