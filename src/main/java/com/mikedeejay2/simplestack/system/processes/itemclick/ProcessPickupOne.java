package com.mikedeejay2.simplestack.system.processes.itemclick;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ProcessPickupOne extends ItemClickProcess
{
    public ProcessPickupOne(InventoryClickEvent event, Simplestack plugin)
    {
        super(event, plugin);
    }

    @Override
    public void execute()
    {
        int newAmount = cursorAmt + 1;
        int extraAmount = selectedAmt - 1;
        if(newAmount > selectedMax || extraAmount < 0)
        {
            return;
        }
        if(cursorNull) cursor = selected.clone();
        cursor.setAmount(newAmount);
        selected.setAmount(extraAmount);
        player.setItemOnCursor(cursor);
    }
}
