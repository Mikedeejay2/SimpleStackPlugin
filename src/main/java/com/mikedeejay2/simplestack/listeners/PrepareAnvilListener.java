package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.mikedeejay2lib.PluginBase;
import com.mikedeejay2.mikedeejay2lib.util.PluginInstancer;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class PrepareAnvilListener extends PluginInstancer<Simplestack> implements Listener
{
    public PrepareAnvilListener(Simplestack plugin)
    {
        super(plugin);
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
        ItemStack result = event.getResult();
        ItemStack item1 = inv.getItem(0);
        ItemStack item2 = inv.getItem(1);
        if(item2 == null || result == null || item1 == null) return;
        if(item1.getAmount() < item2.getAmount())
        {
            result.setAmount(item1.getAmount());
        }
        else if(item1.getAmount() > item2.getAmount())
        {
            result.setAmount(item2.getAmount());
        }
        else if(item1.getAmount() == item2.getAmount())
        {
            result.setAmount(item1.getAmount());
        }
    }
}
