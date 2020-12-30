package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ProcessPlaceAll extends ItemClickProcess
{
    public ProcessPlaceAll(InventoryClickEvent event, Simplestack plugin)
    {
        super(event, plugin);
    }

    @Override
    public void execute()
    {
        int newAmount = selectedAmt + cursorAmt;
        int extraAmount = 0;
        if(newAmount > cursorMax)
        {
            extraAmount = newAmount - cursorMax;
            newAmount = cursorMax;
        }
        if(selectedNull) selected = cursor.clone();
        selected.setAmount(newAmount);
        cursor.setAmount(extraAmount);
        clickedInv.setItem(slot, selected);
        player.setItemOnCursor(cursor);
    }
}
