package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ProcessCloneStack extends ItemClickProcess
{
    public ProcessCloneStack(InventoryClickEvent event, Simplestack plugin)
    {
        super(event, plugin);
    }

    @Override
    public void execute()
    {
        cursor = selected.clone();
        cursor.setAmount(selectedMax);
        player.setItemOnCursor(cursor);
    }
}
