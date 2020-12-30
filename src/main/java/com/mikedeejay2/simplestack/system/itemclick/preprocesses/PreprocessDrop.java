package com.mikedeejay2.simplestack.system.itemclick.preprocesses;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.util.InvActionStruct;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;

public class PreprocessDrop extends ItemClickPreprocess
{
    @Override
    protected void invoke(ItemClickInfo info, InvActionStruct action)
    {
        if(info.clickType != ClickType.DROP) return;
        action.setAction(InventoryAction.DROP_ONE_SLOT);
    }
}
