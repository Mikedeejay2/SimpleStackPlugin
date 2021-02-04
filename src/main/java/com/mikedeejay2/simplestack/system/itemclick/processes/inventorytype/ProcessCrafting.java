package com.mikedeejay2.simplestack.system.itemclick.processes.inventorytype;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.CheckUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProcessCrafting implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.rawSlot != 0) return;
        /* DEBUG */ System.out.println("Process Crafting");
        if(!CheckUtils.takeResult(info)) return;

        Inventory inventory = info.topInv;
        ItemStack result = inventory.getItem(0);
        if(result == null) return;
    }
}
