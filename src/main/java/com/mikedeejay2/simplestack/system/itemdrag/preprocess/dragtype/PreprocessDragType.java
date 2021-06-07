package com.mikedeejay2.simplestack.system.itemdrag.preprocess.dragtype;

import com.mikedeejay2.simplestack.system.itemdrag.ItemDragInfo;
import com.mikedeejay2.simplestack.system.itemdrag.ItemDragType;
import com.mikedeejay2.simplestack.system.itemdrag.preprocess.ItemDragPreprocess;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PreprocessDragType implements ItemDragPreprocess
{
    protected Map<ItemDragType, List<ItemDragPreprocess>> dragPreprocesses;

    public PreprocessDragType()
    {
        this.dragPreprocesses = new EnumMap<>(ItemDragType.class);
        for(ItemDragType type : ItemDragType.values())
        {
            this.dragPreprocesses.put(type, new ArrayList<>());
        }
    }

    public PreprocessDragType addPreprocess(ItemDragType type, ItemDragPreprocess process)
    {
        dragPreprocesses.get(type).add(process);
        return this;
    }

    @Override
    public void invoke(ItemDragInfo info)
    {
        dragPreprocesses.get(info.getType()).forEach(process -> process.invoke(info));
    }
}
