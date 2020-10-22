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
        Item item = event.getEntity();
        ItemStack stack = item.getItemStack();
        Material material = stack.getType();
        if(plugin.stackUtils().getMaxAmount(material) == material.getMaxStackSize()) return;
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(item.isDead() || item.getItemStack().getType() == Material.AIR)
                {
                    this.cancel();
                    return;
                }
                List<Entity> nearby = item.getNearbyEntities(1, 1, 1);
                for(Entity entity : nearby)
                {
                    if(!(entity instanceof Item)) continue;
                    Item newItem = (Item) entity;
                    ItemStack newStack = newItem.getItemStack();
                    if(!plugin.stackUtils().equalsEachOther(stack, newStack)) continue;
                    int stackAmt = stack.getAmount();
                    int newStackAmt = newStack.getAmount();
                    plugin.moveUtils().mergeItems(stack, newStack);
                    if(stackAmt == stack.getAmount() && newStackAmt == newStack.getAmount())
                    {
                        this.cancel();
                        return;
                    }
                    if(stack.getType() == Material.AIR)
                    {
                        this.cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}