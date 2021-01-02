package com.mikedeejay2.simplestack.system.itemclick.preprocesses.clicktype;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocesses.ItemClickPreprocess;
import org.bukkit.event.inventory.InventoryAction;

import java.util.Map;

public class PreprocessRight implements ItemClickPreprocess
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
                    info.setAction(InventoryAction.PLACE_ONE);
                }
            }
            else if(info.selectedAmt <= info.selectedMax)
            {
                Map.Entry<Boolean, Boolean> allowed = InventoryIdentifiers.applicableForSlot(info.rawSlot, info.invView, info.cursor.getType());
                if(!allowed.getKey() && !allowed.getValue()) return;
                info.setAction(InventoryAction.SWAP_WITH_CURSOR);
            }
        }
        else if(!info.cursorNull)
        {
            if(info.validSlot)
            {
                Map.Entry<Boolean, Boolean> allowed = InventoryIdentifiers.applicableForSlot(info.rawSlot, info.invView, info.cursor.getType());
                if(!allowed.getKey() && !allowed.getValue()) return;
                info.setAction(InventoryAction.PLACE_ONE);
            }
            else if(info.clickedOutside)
            {
                info.setAction(InventoryAction.DROP_ONE_CURSOR);
            }
        }
        else if(!info.selectedNull)
        {
            info.setAction(InventoryAction.PICKUP_HALF);
        }
    }
}
