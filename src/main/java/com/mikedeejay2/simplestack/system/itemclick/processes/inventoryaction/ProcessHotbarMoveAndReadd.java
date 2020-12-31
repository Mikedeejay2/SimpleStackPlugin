package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.inventory.ItemStack;

public class ProcessHotbarMoveAndReadd implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        ItemStack hotbarItem = info.bottomInv.getItem(info.hotbar);
        int       hotbarAmt  = hotbarItem.getAmount();
        int       hotbarMax  = StackUtils.getMaxAmount(info.plugin, hotbarItem);
        if(hotbarAmt > hotbarMax || info.selectedAmt > info.selectedMax) return;
        info.clickedInv.setItem(info.slot, hotbarItem);
        info.bottomInv.setItem(info.hotbar, info.selected);
    }
}
