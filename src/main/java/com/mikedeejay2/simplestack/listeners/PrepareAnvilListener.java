package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.CheckUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;

/**
 * Listens for Prepare Anvil events
 *
 * @author Mikedeejay2
 */
public class PrepareAnvilListener implements Listener
{
    private final Simplestack plugin;

    public PrepareAnvilListener(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Patches bug where anvil number counts aren't accurate to what is
     * actually being output.
     *
     * @param event The event being activated
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void prepareAnvilEvent(PrepareAnvilEvent event)
    {
        AnvilInventory inv = event.getInventory();
        CheckUtils.prepareSmithingAnvil(event.getResult(), inv.getItem(0), inv.getItem(1));
    }
}
