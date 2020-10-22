package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;

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
    @EventHandler
    public void prepareAnvilEvent(PrepareAnvilEvent event)
    {
        AnvilInventory inv = event.getInventory();
        plugin.checkUtils().prepareSmithingAnvil(event.getResult(), inv.getItem(0), inv.getItem(1));
    }
}
