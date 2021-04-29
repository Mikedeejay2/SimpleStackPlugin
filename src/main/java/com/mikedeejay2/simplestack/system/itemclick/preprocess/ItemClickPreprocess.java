package com.mikedeejay2.simplestack.system.itemclick.preprocess;

import com.mikedeejay2.simplestack.system.SimpleStackPreprocess;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;

public interface ItemClickPreprocess extends SimpleStackPreprocess
{
    void invoke(ItemClickInfo info);
}
