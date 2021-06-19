package com.mikedeejay2.simplestack.system.itemdrag.process.global;

import com.mikedeejay2.simplestack.system.itemdrag.ItemDragInfo;
import com.mikedeejay2.simplestack.system.itemdrag.ItemDragType;
import com.mikedeejay2.simplestack.system.itemdrag.process.ItemDragProcess;
import com.mikedeejay2.simplestack.system.itemdrag.process.dragtype.ProcessClone;
import com.mikedeejay2.simplestack.system.itemdrag.process.dragtype.ProcessEven;
import com.mikedeejay2.simplestack.system.itemdrag.process.dragtype.ProcessSingle;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ProcessDragType implements ItemDragProcess
{
    protected Map<ItemDragType, List<ItemDragProcess>> dragProcesses;

    public ProcessDragType()
    {
        this.dragProcesses = new EnumMap<>(ItemDragType.class);
        for(ItemDragType action : ItemDragType.values())
        {
            dragProcesses.put(action, new ArrayList<>());
        }
    }

    public void initDefault()
    {
        addProcess(ItemDragType.SINGLE, new ProcessSingle());
        addProcess(ItemDragType.EVEN, new ProcessEven());
        addProcess(ItemDragType.CLONE, new ProcessClone());
    }

    @Override
    public void invoke(ItemDragInfo info)
    {
        dragProcesses.get(info.getType()).forEach(process -> process.invoke(info));
    }

    public ProcessDragType addProcess(ItemDragType action, ItemDragProcess process)
    {
        dragProcesses.get(action).add(process);
        return this;
    }
}
