package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class EntityPickupItemListener implements Listener
{
    private final Simplestack plugin;

    public EntityPickupItemListener(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * EntityPickupItemEvent
     * This is for when multiple unstackable items are on the ground and
     * are picked up by a player.
     * This code will automatically stack them in their inventory as if they
     * were a stack of 64.
     *
     * @param event The event being activated
     */
    @EventHandler
    public void entityPickupItemEvent(EntityPickupItemEvent event)
    {
        LivingEntity entity = event.getEntity();
        if(entity instanceof Player)
        {
            Player player = (Player)event.getEntity();
            if(plugin.cancelUtils().cancelPlayerCheck(player)) return;
        }
        ItemStack item = event.getItem().getItemStack();

        boolean cancel = plugin.cancelUtils().cancelStackCheck(item.getType());
        if(cancel) return;

        boolean success = plugin.moveUtils().moveItemToInventory(event, event.getItem(), entity, item);
        if(!success) event.setCancelled(true);
    }
}
