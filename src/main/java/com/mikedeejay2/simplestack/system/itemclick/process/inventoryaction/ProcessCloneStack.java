package com.mikedeejay2.simplestack.system.itemclick.process.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.process.ItemClickProcess;
import org.bukkit.inventory.ItemStack;

public class ProcessCloneStack implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        ItemStack newCursor = info.selected.clone();
        newCursor.setAmount(info.selectedMax);
        info.player.setItemOnCursor(newCursor);
    }
}
