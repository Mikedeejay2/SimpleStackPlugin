package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.StackUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ProcessHotbarSwap implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        ItemStack hotbarItem = info.bottomInv.getItem(info.hotbar);
        if(hotbarItem != null && hotbarItem.getType() == Material.AIR) hotbarItem = null;
        boolean hotbarNull = hotbarItem == null;
        int hotbarAmt = hotbarNull ? 0 : hotbarItem.getAmount();
        int hotbarMax = hotbarNull ? 0 : StackUtils.getMaxAmount(info.plugin, hotbarItem);
        if(hotbarAmt > hotbarMax || info.selectedAmt > info.selectedMax) return;
        info.clickedInv.setItem(info.slot, hotbarItem);
        info.bottomInv.setItem(info.hotbar, info.selected);
    }
}
