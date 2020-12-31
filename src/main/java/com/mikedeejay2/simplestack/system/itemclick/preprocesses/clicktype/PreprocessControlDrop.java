package com.mikedeejay2.simplestack.system.itemclick.preprocesses.clicktype;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocesses.ItemClickPreprocess;
import org.bukkit.event.inventory.InventoryAction;

public class PreprocessControlDrop implements ItemClickPreprocess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        info.setAction(InventoryAction.DROP_ALL_SLOT);
    }
}
