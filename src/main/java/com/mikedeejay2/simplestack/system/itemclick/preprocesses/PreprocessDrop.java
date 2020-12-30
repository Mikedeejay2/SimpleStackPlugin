package com.mikedeejay2.simplestack.system.itemclick.preprocesses;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.util.InvActionStruct;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;

public class PreprocessDrop implements ItemClickPreprocess
{
    @Override
    public void invoke(ItemClickInfo info, InvActionStruct action)
    {
        action.setAction(InventoryAction.DROP_ONE_SLOT);
    }
}
