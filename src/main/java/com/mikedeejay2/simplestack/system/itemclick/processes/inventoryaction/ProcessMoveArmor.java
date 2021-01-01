package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;

public class ProcessMoveArmor implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        info.player.sendMessage("Move to Armor");
    }
}
