package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.CancelUtils;
import com.mikedeejay2.simplestack.util.CheckUtils;
import com.mikedeejay2.simplestack.util.ClickUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * Listens for Inventory Click events
 *
 * @author Mikedeejay2
 */
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
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void stackEvent(InventoryClickEvent event)
    {
        Player          player    = (Player) event.getWhoClicked();
        InventoryAction action    = event.getAction();
        CheckUtils.updateGUIManual(plugin, player.getOpenInventory().getTopInventory());
        if(CancelUtils.cancelPlayerCheck(plugin, player)) return;
        ItemStack     itemPickUp  = event.getCurrentItem();
        ItemStack     itemPutDown = event.getCursor();
        ClickType     clickType   = event.getClick();
        InventoryView view        = player.getOpenInventory();
        Inventory     topInv      = view.getTopInventory();
        Inventory     bottomInv   = view.getBottomInventory();
        int           slot        = event.getSlot();
        Inventory     clickedInv  = event.getClickedInventory();
        if(itemPickUp == null || action.toString().contains("DROP") || clickType == ClickType.CREATIVE) return;

        boolean cancel1 = CancelUtils.cancelStackCheck(plugin, itemPickUp);
        boolean cancel2 = CancelUtils.cancelStackCheck(plugin, itemPutDown);
        boolean cancel3 = CancelUtils.cancelGUICheck(plugin, topInv, itemPutDown);
        if((cancel1 && cancel2) || event.isCancelled() || cancel3)
        {
            return;
        }
        event.setCancelled(true);

        CheckUtils.useGUICheck(plugin, player, topInv, slot, clickedInv, clickType);

        if(action == InventoryAction.CLONE_STACK)
        {
            ClickUtils.cloneStack(plugin, player, itemPickUp);
        }
        else if(action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.HOTBAR_MOVE_AND_READD)
        {
            event.setCancelled(false);
            return;
        }
        switch(clickType)
        {
            case LEFT:
                ClickUtils.leftClick(plugin, itemPickUp, itemPutDown, player, event);
                break;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:
                ClickUtils.shiftClick(plugin, itemPickUp, player, event);
                break;
            case RIGHT:
                ClickUtils.rightClick(plugin, itemPickUp, itemPutDown, player, event);
                break;
        }
    }
}
