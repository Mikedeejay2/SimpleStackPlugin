package com.mikedeejay2.simplestack.system.itemclick.process.inventorytype;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.process.ItemClickProcess;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProcessLoom implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.rawSlot != 3) return;
        if(!InventoryIdentifiers.takeResult(info.getAction())) return;

        Inventory inventory = info.topInv;
        ItemStack result    = inventory.getItem(3);
        if(result == null) return;
        boolean useMax = info.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY;
        int takeValue = 1;
        if(useMax)
        {
            int maxTake = Integer.MAX_VALUE;
            for(int i = 0; i < inventory.getSize() - 1; ++i)
            {
                ItemStack curItem = inventory.getItem(i);
                if(curItem == null) continue;
                if(curItem.getAmount() == 0) continue;
                maxTake = Math.min(maxTake, curItem.getAmount());
            }
            takeValue = MoveUtils.resultSlotShift(info, maxTake);
        }
        else
        {
            MoveUtils.storeExtra(info, result);
        }

        for(int i = 0; i < inventory.getSize() - 1; ++i)
        {
            ItemStack curItem = inventory.getItem(i);
            if(curItem == null) continue;
            if(curItem.getAmount() == 0) continue;
            curItem.setAmount(curItem.getAmount() - takeValue);
        }
    }
}