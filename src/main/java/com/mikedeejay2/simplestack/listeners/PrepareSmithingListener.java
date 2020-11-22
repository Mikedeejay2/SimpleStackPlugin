package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.CheckUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.SmithingInventory;

/**
 * Listens for Prepare Smithing events
 *
 * @author Mikedeejay2
 */
public class PrepareSmithingListener implements Listener
{
    private final Simplestack plugin;

    public PrepareSmithingListener(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Patches bug where smithing table number counts aren't accurate to what is
     * actually being output.
     *
     * @param event The event being activated
     */
    @EventHandler
    public void prepareSmithingEvent(PrepareSmithingEvent event)
    {
        SmithingInventory inv = event.getInventory();
        CheckUtils.prepareSmithingAnvil(event.getResult(), inv.getItem(0), inv.getItem(1));
    }
}
