package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ItemSpawnListener implements Listener
{
    private final Simplestack plugin;

    public ItemSpawnListener(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     *
     *
     * @param event The event being activated
     */
    @EventHandler
    public void itemSpawnEvent(ItemSpawnEvent event)
    {

    }
}