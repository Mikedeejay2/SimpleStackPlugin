package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.SimpleStackProcess;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickContainer;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class ItemClickProcess extends ItemClickContainer implements SimpleStackProcess
{
    public ItemClickProcess(InventoryClickEvent event, Simplestack plugin)
    {
        super(event, plugin);
    }

    public abstract void invoke();
}
