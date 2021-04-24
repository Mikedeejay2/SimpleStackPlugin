package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.CheckUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingInventory;

/**
 * Listens for Prepare Item Craft events <p>
 * <b>Currently unused</b>
 *
 * @author Mikedeejay2
 */
public class PrepareItemCraftListener implements Listener
{
    private final Simplestack plugin;

    public PrepareItemCraftListener(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Currently unused
     *
     * @param event The event being activated
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void prepareItemCraftEvent(PrepareItemCraftEvent event)
    {

    }
}
