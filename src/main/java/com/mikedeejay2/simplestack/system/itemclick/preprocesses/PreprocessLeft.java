package com.mikedeejay2.simplestack.system.itemclick.preprocesses;

import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.util.InvActionStruct;
import org.bukkit.event.inventory.InventoryAction;

public class PreprocessLeft extends ItemClickPreprocess
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
                    int totalAmt = info.cursorAmt + info.selectedAmt;
                    if(totalAmt == info.selectedAmt + 1 && info.cursorAmt != 1)
                    {
                        action.setAction(InventoryAction.PLACE_ONE);
                    }
                    else if(totalAmt > info.selectedMax)
                    {
                        action.setAction(InventoryAction.PLACE_SOME);
                    }
                    else
                    {
                        action.setAction(InventoryAction.PLACE_ALL);
                    }
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
                if(info.cursorAmt <= info.cursorMax)
                {
                    action.setAction(InventoryAction.PLACE_ALL);
                }
                else if(info.cursorMax == 1)
                {
                    action.setAction(InventoryAction.PLACE_ONE);
                }
                else
                {
                    action.setAction(InventoryAction.PLACE_SOME);
                }
            }
            else if(info.clickedOutside)
            {
                action.setAction(InventoryAction.DROP_ALL_CURSOR);
            }
        }
        else if(!info.selectedNull)
        {
            if(info.selectedAmt <= info.selectedMax)
            {
                action.setAction(InventoryAction.PICKUP_ALL);
            }
            else if(info.selectedMax == 1)
            {
                action.setAction(InventoryAction.PICKUP_ONE);
            }
            else
            {
                action.setAction(InventoryAction.PICKUP_SOME);
            }
        }
    }
}
