package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import org.bukkit.inventory.ItemStack;

public class ProcessPlaceAll implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        int newAmount = info.selectedAmt + info.cursorAmt;
        int extraAmount = 0;
        if(newAmount > info.cursorMax)
        {
            extraAmount = newAmount - info.cursorMax;
            newAmount = info.cursorMax;
        }
        ItemStack newSelected = info.selected;
        if(info.selectedNull) newSelected = info.cursor.clone();
        newSelected.setAmount(newAmount);
        info.cursor.setAmount(extraAmount);
        info.clickedInv.setItem(info.slot, newSelected);
        info.player.setItemOnCursor(info.cursor);
    }
}
