package com.mikedeejay2.simplestack.system.itemclick.preprocesses;

import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.util.InvActionStruct;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;

public class PreprocessShift implements ItemClickPreprocess
{
    @Override
    public void invoke(ItemClickInfo info, InvActionStruct action)
    {
        if(!info.selectedNull)
        {
            action.setAction(InventoryAction.MOVE_TO_OTHER_INVENTORY);
        }
    }
}
