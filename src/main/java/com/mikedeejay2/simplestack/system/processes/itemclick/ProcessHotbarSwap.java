package com.mikedeejay2.simplestack.system.processes.itemclick;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ProcessHotbarSwap extends ItemClickProcess
{
    public ProcessHotbarSwap(InventoryClickEvent event, Simplestack plugin)
    {
        super(event, plugin);
    }

    @Override
    public void execute()
    {
        ItemStack hotbarItem = bottomInv.getItem(hotbar);
        if(hotbarItem != null && hotbarItem.getType() == Material.AIR) hotbarItem = null;
        boolean hotbarNull = hotbarItem == null;
        int hotbarAmt = hotbarNull ? 0 : hotbarItem.getAmount();
        int hotbarMax = hotbarNull ? 0 : StackUtils.getMaxAmount(plugin, hotbarItem);
        if(hotbarAmt > hotbarMax || selectedAmt > selectedMax) return;
        clickedInv.setItem(slot, hotbarItem);
        bottomInv.setItem(hotbar, selected);
    }
}
