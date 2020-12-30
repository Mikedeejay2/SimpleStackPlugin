package com.mikedeejay2.simplestack.system.itemclick.preprocesses;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.util.InvActionStruct;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public class PreprocessDoubleClick extends ItemClickPreprocess
{
    @Override
    public void invoke(ItemClickInfo info, InvActionStruct action)
    {
        action.setAction(InventoryAction.COLLECT_TO_CURSOR);
    }
}