package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.mikedeejay2lib.PluginBase;
import com.mikedeejay2.mikedeejay2lib.util.PluginInstancer;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;

public class PrepareSmithingListener extends PluginInstancer<Simplestack> implements Listener
{
    public PrepareSmithingListener(Simplestack plugin)
    {
        super(plugin);
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
        plugin.checkUtils().prepareSmithingAnvil(event.getResult(), inv.getItem(0), inv.getItem(1));
    }
}
