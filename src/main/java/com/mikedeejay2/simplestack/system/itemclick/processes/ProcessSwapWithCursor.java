package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ProcessSwapWithCursor extends ItemClickProcess
{
    public ProcessSwapWithCursor(InventoryClickEvent event, Simplestack plugin)
    {
        super(event, plugin);
    }

    @Override
    public void execute()
    {
        if(selectedAmt > selectedMax || cursorAmt > cursorMax)
        {
            return;
        }
        clickedInv.setItem(slot, cursor);
        player.setItemOnCursor(selected);
    }
}
