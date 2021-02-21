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
        if(info.clickedInvType == null) return;
        switch(info.clickedInvType)
        {
            case WORKBENCH:
            case CRAFTING:
            case LOOM:
            {
                Inventory inventory = info.clickedInv;
                inventory.setItem(1, inventory.getItem(1));
            }
            break;
        }
    }
}
