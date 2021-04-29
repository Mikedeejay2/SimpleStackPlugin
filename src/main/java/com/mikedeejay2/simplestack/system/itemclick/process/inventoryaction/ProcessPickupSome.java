package com.mikedeejay2.simplestack.system.itemclick.process.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.process.ItemClickProcess;
import org.bukkit.inventory.ItemStack;

public class ProcessPickupSome implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        int newAmount = info.cursorAmt + info.selectedAmt;
        int extraAmount = 0;
        if(newAmount > info.selectedMax)
        {
            extraAmount = newAmount - info.selectedMax;
            newAmount = info.selectedMax;
        }
        ItemStack newCursor = info.cursor;
        if(info.cursorNull) newCursor = info.selected.clone();
        newCursor.setAmount(newAmount);
        info.selected.setAmount(extraAmount);
        info.player.setItemOnCursor(newCursor);
    }
}
