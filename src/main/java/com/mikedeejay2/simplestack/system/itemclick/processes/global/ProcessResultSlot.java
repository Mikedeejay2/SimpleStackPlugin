package com.mikedeejay2.simplestack.system.itemclick.processes.global;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.CheckUtils;
import org.bukkit.event.inventory.InventoryType;

public class ProcessResultSlot implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.slotType != InventoryType.SlotType.RESULT) return;
        if(!CheckUtils.takeResult(info)) return;
    }
}
