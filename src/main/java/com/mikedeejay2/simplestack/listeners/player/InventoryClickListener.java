package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.itemclick.handlers.ItemClickHandler;
import com.mikedeejay2.simplestack.util.CancelUtils;
import com.mikedeejay2.simplestack.util.CheckUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
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
    protected ItemClickHandler handler;

    public InventoryClickListener(Simplestack plugin)
    {
        this.plugin = plugin;
        this.handler = new ItemClickHandler(plugin);
        handler.initDefault();
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
        Player                 player      = (Player) event.getWhoClicked();
        InventoryAction        action      = event.getAction();
        CheckUtils.updateGUIManual(plugin, player.getOpenInventory().getTopInventory());
        if(CancelUtils.cancelPlayerCheck(plugin, player)) return;
        ItemStack              itemPickUp  = event.getCurrentItem();
        ItemStack              itemPutDown = event.getCursor();
        ClickType              clickType   = event.getClick();
        InventoryView          view        = player.getOpenInventory();
        Inventory              topInv      = view.getTopInventory();
        Inventory              bottomInv   = view.getBottomInventory();
        int                    slot        = event.getSlot();
        InventoryType.SlotType slotType    = event.getSlotType();
        Inventory              clickedInv  = event.getClickedInventory();
        if(clickType == ClickType.CREATIVE || (plugin.getMCVersion().getVersionShort() >= 16 && clickType == ClickType.SWAP_OFFHAND)) return;
        event.setCancelled(true);
        if(plugin.getDebugConfig().isPrintAction())
        {
            player.sendMessage(
                    "\n\n" +
                    "Action: " + action + "\n" +
                    "ClickType: " + clickType + "\n" +
                    "Slot: " + slot + "\n" +
                    "RawSlot: " + event.getRawSlot() + "\n" +
                    "HotBar Slot: " + event.getHotbarButton() + "\n" +
                    "Selected Item: " + itemPickUp + "\n" +
                    "Cursor Item: " + itemPutDown + "\n" +
                    "Slot Type: " + slotType + "\n"
            );
        }
            handler.handle(event);
    }
}
