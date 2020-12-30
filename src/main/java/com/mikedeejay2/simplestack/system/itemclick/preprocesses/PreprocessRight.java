package com.mikedeejay2.simplestack.system.itemclick.preprocesses;

import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.util.InvActionStruct;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;

public class PreprocessRight extends ItemClickPreprocess
{
    @Override
    public void invoke(ItemClickInfo info, InvActionStruct action)
    {
        if(!info.cursorNull && !info.selectedNull)
        {
            if(ItemComparison.equalsEachOther(info.cursor, info.selected))
            {
                if(info.selectedAmt < info.selectedMax)
                {
                    action.setAction(InventoryAction.PLACE_ONE);
                }
            }
            else if(info.selectedAmt <= info.selectedMax)
            {
                action.setAction(InventoryAction.SWAP_WITH_CURSOR);
            }
        }
        else if(!info.cursorNull)
        {
            if(info.validSlot)
            {
                action.setAction(InventoryAction.PLACE_ONE);
            }
            else if(info.clickedOutside)
            {
                action.setAction(InventoryAction.DROP_ONE_CURSOR);
            }
        }
        else if(!info.selectedNull)
        {
            action.setAction(InventoryAction.PICKUP_HALF);
        }
    }
}
