package com.mikedeejay2.simplestack.listeners;

import com.mikedeejay2.mikedeejay2lib.util.PluginInstancer;
import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;

public class ItemMergeListener extends PluginInstancer<Simplestack> implements Listener
{
    public ItemMergeListener(Simplestack plugin)
    {
        super(plugin);
    }

    /**
     * Patches bug where stackable items that are set to a lower value than regular
     * can be merged into a larger number when dropped.
     *
     * @param event The event being activated
     */
    @EventHandler
    public void itemMergeEvent(ItemMergeEvent event)
    {
        Item resultItem = event.getEntity();
        Item targetItem = event.getTarget();
        event.setCancelled(true);
        ItemStack resultStack = resultItem.getItemStack();
        ItemStack targetStack = targetItem.getItemStack();
        int maxAmountInStack = plugin.stackUtils().getMaxAmount(resultStack.getType());
        int newAmount = resultStack.getAmount() + targetStack.getAmount();
        int extraAmount = 0;
        if(newAmount > maxAmountInStack)
        {
            extraAmount = (newAmount - maxAmountInStack);
            newAmount = maxAmountInStack;
        }
        resultStack.setAmount(extraAmount);
        targetStack.setAmount(newAmount);
    }
}
