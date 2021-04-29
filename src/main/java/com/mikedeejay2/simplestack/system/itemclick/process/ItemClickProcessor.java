package com.mikedeejay2.simplestack.system.itemclick.process;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.generic.ProcessorBase;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.process.global.ProcessForceUpdate;

public class ItemClickProcessor extends ProcessorBase<ItemClickProcess, ItemClickInfo, ItemClickProcessor>
{
    public ItemClickProcessor(Simplestack plugin)
    {
        super(plugin);
    }

    @Override
    public void initDefault()
    {
        ProcessInvType invType = new ProcessInvType();
        invType.initDefault();
        addProcess(invType);

        ProcessAction action = new ProcessAction();
        action.initDefault();
        addProcess(action);

        ProcessForceUpdate update = new ProcessForceUpdate();
        addProcess(update);
    }
}
