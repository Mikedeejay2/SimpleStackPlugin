package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.StackUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener
{
    // Plugin instance for referencing
    private static final Simplestack plugin = Simplestack.getInstance();

    /*
     * InventoryClickEvent handler,
     * This does a majority of the work for this plugin.
     */
    @EventHandler
    public void stackEvent(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        StackUtils.updateAnvilManual(player.getOpenInventory().getTopInventory());
        if(StackUtils.cancelPlayerCheck(player)) return;
        ItemStack itemPickUp = event.getCurrentItem();
        ItemStack itemPutDown = event.getCursor();
        ClickType clickType = event.getClick();
        if(itemPickUp == null || clickType.equals(ClickType.CREATIVE) || event.getAction().equals(InventoryAction.CLONE_STACK) || event.getAction().toString().contains("DROP")) return;

        boolean cancel = StackUtils.cancelStackCheck(itemPickUp.getType());
        if(cancel || event.isCancelled()) return;
        event.setCancelled(true);

        StackUtils.makeUnique(itemPickUp, plugin.getKey());

        switch(clickType)
        {
            case LEFT:
                StackUtils.leftClick(itemPickUp, itemPutDown, player, event);
                break;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                StackUtils.shiftClick(itemPickUp, player, event);
                break;
            case RIGHT:
                StackUtils.rightClick(itemPickUp, itemPutDown, player, event);
                break;
        }
    }
}
