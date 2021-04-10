package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.system.SimpleStackProcess;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;

@FunctionalInterface
public interface ItemClickProcess extends SimpleStackProcess
{
    void invoke(ItemClickInfo info);
}
