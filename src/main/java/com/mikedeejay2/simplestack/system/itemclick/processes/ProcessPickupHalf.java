package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ProcessPickupHalf extends ItemClickProcess
{
    public ProcessPickupHalf(InventoryClickEvent event, Simplestack plugin)
    {
        super(event, plugin);
    }

    @Override
    public void execute()
    {
        int halfSelected = (int) Math.floor(selectedAmt / 2.0);
        int halfCursor = (int) Math.ceil(selectedAmt / 2.0);
        if(halfCursor > selectedMax)
        {
            halfSelected += halfCursor - selectedMax;
            halfCursor = selectedMax;
        }
        if(cursorNull) cursor = selected.clone();
        cursor.setAmount(halfCursor);
        selected.setAmount(halfSelected);
        player.setItemOnCursor(cursor);
    }
}
