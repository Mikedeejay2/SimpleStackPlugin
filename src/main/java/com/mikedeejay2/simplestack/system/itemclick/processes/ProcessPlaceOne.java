package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import org.bukkit.inventory.ItemStack;

public class ProcessPlaceOne implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        int newAmount = info.selectedAmt + 1;
        int extraAmount = info.cursorAmt - 1;
        if(newAmount > info.cursorMax || extraAmount < 0)
        {
            return;
        }
        ItemStack newSelected = info.selected;
        if(info.selectedNull) newSelected = info.cursor.clone();
        newSelected.setAmount(newAmount);
        info.cursor.setAmount(extraAmount);
        info.clickedInv.setItem(info.slot, newSelected);
        info.player.setItemOnCursor(info.cursor);
    }
}
