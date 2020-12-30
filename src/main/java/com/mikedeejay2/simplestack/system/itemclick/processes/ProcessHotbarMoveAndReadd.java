package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ProcessHotbarMoveAndReadd extends ItemClickProcess
{
    public ProcessHotbarMoveAndReadd(InventoryClickEvent event, Simplestack plugin)
    {
        super(event, plugin);
    }

    @Override
    public void invoke()
    {
        ItemStack hotbarItem = bottomInv.getItem(hotbar);
        int       hotbarAmt  = hotbarItem.getAmount();
        int       hotbarMax  = StackUtils.getMaxAmount(plugin, hotbarItem);
        if(hotbarAmt > hotbarMax || selectedAmt > selectedMax) return;
        clickedInv.setItem(slot, hotbarItem);
        bottomInv.setItem(hotbar, selected);
    }
}
