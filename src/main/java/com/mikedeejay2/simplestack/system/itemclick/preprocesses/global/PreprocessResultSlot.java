package com.mikedeejay2.simplestack.system.itemclick.preprocesses.global;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.preprocesses.ItemClickPreprocess;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;

public class PreprocessResultSlot implements ItemClickPreprocess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        InventoryAction action = info.getAction();
        if(info.slotType != InventoryType.SlotType.RESULT) return;
        switch(action)
        {
            case PICKUP_ONE:
            case PICKUP_HALF:
            case PLACE_ALL:
            case PLACE_ONE:
            case PLACE_SOME:
                info.setAction(InventoryAction.PICKUP_ALL);
                break;
        }
    }
}
