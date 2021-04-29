package com.mikedeejay2.simplestack.system.itemclick.preprocess.clicktype;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocess.ItemClickPreprocess;
import org.bukkit.event.inventory.InventoryAction;

public class PreprocessDoubleClick implements ItemClickPreprocess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        info.setAction(InventoryAction.COLLECT_TO_CURSOR);
    }
}
