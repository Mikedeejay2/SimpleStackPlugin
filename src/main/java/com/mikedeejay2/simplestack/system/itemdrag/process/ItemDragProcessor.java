package com.mikedeejay2.simplestack.system.itemdrag.process;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.generic.ProcessorBase;
import com.mikedeejay2.simplestack.system.itemdrag.ItemDragInfo;
import com.mikedeejay2.simplestack.system.itemdrag.process.global.ProcessDragType;

public class ItemDragProcessor extends ProcessorBase<ItemDragProcess, ItemDragInfo, ItemDragProcessor>
{
    public ItemDragProcessor(Simplestack plugin)
    {
        super(plugin);
    }

    @Override
    public void initDefault()
    {
        ProcessDragType dragType = new ProcessDragType();
        dragType.initDefault();
        addProcess(dragType);
    }
}
