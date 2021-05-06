package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listens for Item Spawn events. <p>
 *
 * @author Mikedeejay2
 */
public class ItemSpawnListener implements Listener
{
    private final Simplestack plugin;

    public ItemSpawnListener(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    /**
     * Fix a bug where items had the ability to spawn with too much in a stack.
     *
     * @param event The event being activated
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void itemSpawnEvent(ItemSpawnEvent event)
    {
        Item item = event.getEntity();
        ItemStack itemStack = item.getItemStack();
        if(itemStack.getType() == Material.AIR) return;
        int maxAmt = StackUtils.getMaxAmount(plugin, itemStack);
        if(itemStack.getAmount() <= maxAmt) return;
        int extraAmount = itemStack.getAmount() - maxAmt;
        itemStack.setAmount(itemStack.getAmount() - extraAmount);
        while(extraAmount > 0)
        {
            int newAmount = Math.min(extraAmount, maxAmt);
            extraAmount -= newAmount;
            ItemStack newItemStack = itemStack.clone();
            newItemStack.setAmount(newAmount);
            Item newItem = item.getWorld().dropItem(item.getLocation(), newItemStack);
            newItem.setVelocity(item.getVelocity());
        }
        itemStack.setAmount(maxAmt);
    }
}