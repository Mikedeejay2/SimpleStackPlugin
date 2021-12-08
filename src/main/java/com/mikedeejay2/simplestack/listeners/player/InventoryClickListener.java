package com.mikedeejay2.simplestack.listeners.player;

import com.mikedeejay2.mikedeejay2lib.util.version.MinecraftVersion;
import com.mikedeejay2.simplestack.Simplestack;
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
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void stackEvent(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        InventoryAction action = event.getAction();
        if(CancelUtils.cancelPlayerCheck(plugin, player)) return;
        ItemStack selectedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        ClickType clickType = event.getClick();
        int slot = event.getSlot();
        InventoryType.SlotType slotType = event.getSlotType();
        if(clickType == ClickType.CREATIVE || (MinecraftVersion.getVersionShort() >= 16 && clickType == ClickType.SWAP_OFFHAND)) return;
        if(CancelUtils.cancelStackCheck(plugin, selectedItem) || CancelUtils.cancelStackCheck(plugin, cursorItem)) return;
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
                    "Selected Item: " + selectedItem + "\n" +
                    "Cursor Item: " + cursorItem + "\n" +
                    "Slot Type: " + slotType + "\n"
            );
        }
        plugin.getItemClickHandler().handle(event);
    }
}
