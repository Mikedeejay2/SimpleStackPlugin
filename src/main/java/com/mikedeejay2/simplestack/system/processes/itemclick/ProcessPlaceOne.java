package com.mikedeejay2.simplestack.system.processes.itemclick;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ProcessPlaceOne extends ItemClickProcess
{
    public ProcessPlaceOne(InventoryClickEvent event, Simplestack plugin)
    {
        super(event, plugin);
    }

    @Override
    public void execute()
    {
        int newAmount = selectedAmt + 1;
        int extraAmount = cursorAmt - 1;
        if(newAmount > cursorMax || extraAmount < 0)
        {
            return;
        }
        if(selectedNull) selected = cursor.clone();
        selected.setAmount(newAmount);
        cursor.setAmount(extraAmount);
        clickedInv.setItem(slot, selected);
        player.setItemOnCursor(cursor);
    }
}
