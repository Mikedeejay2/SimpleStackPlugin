package com.mikedeejay2.simplestack.system.itemclick.process.inventorytype;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.process.ItemClickProcess;
import com.mikedeejay2.simplestack.util.MoveUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProcessCrafting implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        if(info.rawSlot != 0) return;
        if(!InventoryIdentifiers.takeResult(info.getAction())) return;

        Inventory inventory = info.topInv;
        ItemStack result = inventory.getItem(0);
        if(result == null) return;
        boolean useMax = info.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY;
        int takeValue = 1;
        if(useMax)
        {
            int maxTake = Integer.MAX_VALUE;
            for(int i = 1; i < inventory.getSize(); ++i)
            {
                ItemStack curStack = inventory.getItem(i);
                if(curStack == null || curStack.getType() == Material.AIR) continue;
                int curAmt = curStack.getAmount();
                if(curAmt >= maxTake) continue;
                maxTake = curAmt;
            }
            takeValue = MoveUtils.resultSlotShift(info, maxTake);
        }
        else
        {
            MoveUtils.storeExtra(info, result);
        }

        for(int i = 1; i < inventory.getSize(); ++i)
        {
            ItemStack curItem = inventory.getItem(i);
            if(curItem == null || curItem.getType() == Material.AIR) continue;
            if(curItem.getAmount() == 0) continue;
            int newAmount = curItem.getAmount() - takeValue;
            curItem.setAmount(newAmount);
        }
    }
}
