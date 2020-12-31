package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;

public class ProcessSwapWithCursor implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.selectedAmt > info.selectedMax || info.cursorAmt > info.cursorMax)
        {
            return;
        }
        info.clickedInv.setItem(info.slot, info.cursor);
        info.player.setItemOnCursor(info.selected);
    }
}
