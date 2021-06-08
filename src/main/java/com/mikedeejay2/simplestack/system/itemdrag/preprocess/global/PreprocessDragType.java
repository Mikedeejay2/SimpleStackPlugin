package com.mikedeejay2.simplestack.system.itemdrag.preprocess.global;

import com.mikedeejay2.simplestack.system.itemdrag.ItemDragInfo;
import com.mikedeejay2.simplestack.system.itemdrag.ItemDragType;
import com.mikedeejay2.simplestack.system.itemdrag.preprocess.ItemDragPreprocess;
import com.mikedeejay2.simplestack.system.itemdrag.preprocess.dragtype.PreprocessClone;
import com.mikedeejay2.simplestack.system.itemdrag.preprocess.dragtype.PreprocessEven;
import com.mikedeejay2.simplestack.system.itemdrag.preprocess.dragtype.PreprocessSingle;
import org.bukkit.event.inventory.DragType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PreprocessDragType implements ItemDragPreprocess
{
    protected Map<DragType, List<ItemDragPreprocess>> dragPreprocesses;

    public PreprocessDragType()
    {
        this.dragPreprocesses = new EnumMap<>(DragType.class);
        for(DragType type : DragType.values())
        {
            this.dragPreprocesses.put(type, new ArrayList<>());
        }
    }

    public void initDefault()
    {
        addPreprocess(DragType.SINGLE, new PreprocessSingle());
        addPreprocess(DragType.EVEN, new PreprocessEven());
        addPreprocess(DragType.EVEN, new PreprocessClone());
    }

    public PreprocessDragType addPreprocess(DragType type, ItemDragPreprocess process)
    {
        dragPreprocesses.get(type).add(process);
        return this;
    }

    @Override
    public void invoke(ItemDragInfo info)
    {
        dragPreprocesses.get(info.oldType).forEach(process -> process.invoke(info));
    }
}
