package com.mikedeejay2.simplestack.system.itemdrag.preprocess.dragtype;

import com.mikedeejay2.simplestack.system.itemdrag.ItemDragInfo;
import com.mikedeejay2.simplestack.system.itemdrag.ItemDragType;
import com.mikedeejay2.simplestack.system.itemdrag.preprocess.ItemDragPreprocess;

public class PreprocessSingle implements ItemDragPreprocess
{
    @Override
    public void invoke(ItemDragInfo data)
    {
        data.setType(ItemDragType.SINGLE);
    }
}
