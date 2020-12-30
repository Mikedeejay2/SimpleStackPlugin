package com.mikedeejay2.simplestack.system.itemclick.preprocesses;

import com.mikedeejay2.simplestack.system.SimpleStackPreprocess;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.util.InvActionStruct;

public interface ItemClickPreprocess extends SimpleStackPreprocess
{
    void invoke(ItemClickInfo info, InvActionStruct action);
}
