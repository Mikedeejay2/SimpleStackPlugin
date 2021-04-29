package com.mikedeejay2.simplestack.system.itemclick.process.inventorytype;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.process.ItemClickProcess;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProcessFurnace implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.rawSlot != 2) return;
        if(!InventoryIdentifiers.takeResult(info.getAction())) return;
        Inventory inventory = info.topInv;
        ItemStack result = inventory.getItem(2);
        if(result == null) return;
        int resultAmt = result.getAmount();
        boolean useMax = info.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY;
        if(useMax)
        {
            MoveUtils.resultSlotShift(info, 1);
        }
        else
        {
            MoveUtils.storeExtra(info, result);
        }
    }
}
