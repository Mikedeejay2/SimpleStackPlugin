package com.mikedeejay2.simplestack.system.itemdrag.preprocess;

import com.mikedeejay2.simplestack.Simplestack;
import com.mikedeejay2.simplestack.system.generic.PreprocessorBase;
import com.mikedeejay2.simplestack.system.itemdrag.ItemDragInfo;
import com.mikedeejay2.simplestack.system.itemdrag.preprocess.global.PreprocessDragType;

public class ItemDragPreprocessor extends PreprocessorBase<ItemDragPreprocess, ItemDragInfo, ItemDragPreprocessor>
{
    public ItemDragPreprocessor(Simplestack plugin)
    {
        super(plugin);
    }

    @Override
    public void initDefault()
    {
        PreprocessDragType dragType = new PreprocessDragType();
        dragType.initDefault();
        addPreprocess(dragType);
    }
}
