package com.mikedeejay2.simplestack.system.itemdrag.process.dragtype;

import com.mikedeejay2.simplestack.system.itemdrag.ItemDragInfo;
import com.mikedeejay2.simplestack.system.itemdrag.process.ItemDragProcess;

public class ProcessEven implements ItemDragProcess
{
    @Override
    public void invoke(ItemDragInfo data)
    {
        System.out.println("Process Even");
    }
}
