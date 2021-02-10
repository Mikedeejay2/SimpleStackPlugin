package com.mikedeejay2.simplestack.system.itemclick.processes.global;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import org.bukkit.inventory.Inventory;

public class ProcessForceUpdate implements ItemClickProcess
{
    protected final Simplestack plugin;

    public ProcessForceUpdate(Simplestack plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void invoke(ItemClickInfo info)
    {
        Inventory inventory = info.clickedInv;
        if(inventory == null) return;
        System.out.println("Inventory: " + inventory.getType());
        switch(inventory.getType())
        {
            case WORKBENCH:
            case CRAFTING:
            {
                info.player.updateInventory();
                return;
            }
        }
    }
}
