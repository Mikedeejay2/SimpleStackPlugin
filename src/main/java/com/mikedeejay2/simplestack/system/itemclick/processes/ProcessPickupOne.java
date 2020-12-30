package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import org.bukkit.inventory.ItemStack;

public class ProcessPickupOne implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        int newAmount = info.cursorAmt + 1;
        int extraAmount = info.selectedAmt - 1;
        if(newAmount > info.selectedMax || extraAmount < 0)
        {
            return;
        }
        ItemStack newCursor = info.cursor;
        if(info.cursorNull) newCursor = info.selected.clone();
        newCursor.setAmount(newAmount);
        info.selected.setAmount(extraAmount);
        info.player.setItemOnCursor(newCursor);
    }
}
