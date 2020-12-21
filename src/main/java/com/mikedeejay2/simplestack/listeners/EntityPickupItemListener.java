package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.CancelUtils;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Listens for Entity Pickup Item events
 *
 * @author Mikedeejay2
 */
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
        if(!(entity instanceof InventoryHolder)) return;
        if(entity instanceof Player)
        {
            Player player = (Player) event.getEntity();
            if(CancelUtils.cancelPlayerCheck(plugin, player)) return;
        }
        ItemStack item = event.getItem().getItemStack();

        boolean cancel = CancelUtils.cancelStackCheck(plugin, item);
        if(cancel) return;

        boolean success = MoveUtils.moveItemToInventory(plugin, event, event.getItem(), entity, item);
        if(!success) event.setCancelled(true);
    }
}
