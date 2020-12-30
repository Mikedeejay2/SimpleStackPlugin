package com.mikedeejay2.simplestack.system.itemclick.preprocesses;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import org.bukkit.event.inventory.InventoryAction;

public class PreprocessShift implements ItemClickPreprocess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(!info.selectedNull)
        {
            info.setAction(InventoryAction.MOVE_TO_OTHER_INVENTORY);
        }
    }
}
