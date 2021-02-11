package com.mikedeejay2.simplestack.system.itemclick.processes.inventorytype;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;

public class ProcessStonecutter implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.rawSlot != 1) return;
        /* DEBUG */ System.out.println("Process Stonecutter");
    }
}