package com.mikedeejay2.simplestack.system.itemclick.preprocesses.clicktype;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocesses.ItemClickPreprocess;
import org.bukkit.event.inventory.InventoryAction;

import java.util.Map;

public class PreprocessLeft implements ItemClickPreprocess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(!info.cursorNull && !info.selectedNull)
        {
            if(ItemComparison.equalsEachOther(info.cursor, info.selected))
            {
                if(info.selectedAmt < info.selectedMax)
                {
                    int totalAmt = info.cursorAmt + info.selectedAmt;
                    if(InventoryIdentifiers.singletonSlot(info.rawSlot, info.invView) && totalAmt > 1) return;
                    if(totalAmt == info.selectedAmt + 1 && info.cursorAmt != 1)
                    {
                        info.setAction(InventoryAction.PLACE_ONE);
                    }
                    else if(totalAmt > info.selectedMax)
                    {
                        info.setAction(InventoryAction.PLACE_SOME);
                    }
                    else
                    {
                        info.setAction(InventoryAction.PLACE_ALL);
                    }
                }
            }
            else if(info.selectedAmt <= info.selectedMax)
            {
                if(InventoryIdentifiers.singletonSlot(info.rawSlot, info.invView) && info.cursorAmt > 1) return;
                info.setAction(InventoryAction.SWAP_WITH_CURSOR);
            }
        }
        else if(!info.cursorNull)
        {
            if(info.validSlot)
            {
                Map.Entry<Boolean, Boolean> allowed = InventoryIdentifiers.applicableForSlot(info.rawSlot, info.invView, info.cursor.getType());
                if(!allowed.getKey() && !allowed.getValue()) return;
                if(InventoryIdentifiers.singletonSlot(info.rawSlot, info.invView) && info.cursorAmt > 1)
                {
                    info.setAction(InventoryAction.PLACE_ONE);
                    return;
                }
                if(info.cursorAmt <= info.cursorMax)
                {
                    info.setAction(InventoryAction.PLACE_ALL);
                }
                else if(info.cursorMax == 1)
                {
                    info.setAction(InventoryAction.PLACE_ONE);
                }
                else
                {
                    info.setAction(InventoryAction.PLACE_SOME);
                }
            }
            else if(info.clickedOutside)
            {
                info.setAction(InventoryAction.DROP_ALL_CURSOR);
            }
        }
        else if(!info.selectedNull)
        {
            if(info.selectedAmt <= info.selectedMax)
            {
                info.setAction(InventoryAction.PICKUP_ALL);
            }
            else if(info.selectedMax == 1)
            {
                info.setAction(InventoryAction.PICKUP_ONE);
            }
            else
            {
                info.setAction(InventoryAction.PICKUP_SOME);
            }
        }
    }
}
