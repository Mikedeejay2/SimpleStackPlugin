package com.mikedeejay2.simplestack.system.processes.itemclick;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ProcessPickupAll extends ItemClickProcess
{
    public ProcessPickupAll(InventoryClickEvent event, Simplestack plugin)
    {
        super(event, plugin);
    }

    @Override
    public void execute()
    {
        int newAmount = cursorAmt + selectedAmt;
        int extraAmount = 0;
        if(newAmount > selectedMax)
        {
            extraAmount = newAmount - selectedMax;
            newAmount = selectedMax;
        }
        if(cursorNull) cursor = selected.clone();
        cursor.setAmount(newAmount);
        selected.setAmount(extraAmount);
        player.setItemOnCursor(cursor);
    }
}
