package com.mikedeejay2.simplestack.system.itemclick.process.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.process.ItemClickProcess;
import org.bukkit.inventory.ItemStack;

public class ProcessPickupHalf implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        int halfSelected = (int) Math.floor(info.selectedAmt / 2.0);
        int halfCursor = (int) Math.ceil(info.selectedAmt / 2.0);
        if(halfCursor > info.selectedMax)
        {
            halfSelected += halfCursor - info.selectedMax;
            halfCursor = info.selectedMax;
        }
        ItemStack newCursor = info.cursor;
        if(info.cursorNull) newCursor = info.selected.clone();
        newCursor.setAmount(halfCursor);
        info.selected.setAmount(halfSelected);
        info.player.setItemOnCursor(newCursor);
    }
}
