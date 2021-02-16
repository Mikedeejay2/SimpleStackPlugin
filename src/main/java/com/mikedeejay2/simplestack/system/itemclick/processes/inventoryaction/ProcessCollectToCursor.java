package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction;

import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class ProcessCollectToCursor implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        InventoryView inventory = info.player.getOpenInventory();
        if(info.cursor == null) return;
        Material cursorMat = info.cursor.getType();
        int cursorAmt = info.cursorAmt;
        for(int amount = 1; amount <= info.cursorMax; ++amount)
        {
            for(int i = 0; i < inventory.countSlots(); ++i)
            {
                ItemStack item = inventory.getItem(i);
                if(item == null) continue;
                if(item.getType() != cursorMat) continue;
                if(item.getAmount() > amount) continue;
                if(!ItemComparison.equalsEachOther(item, info.cursor)) continue;
                if(inventory.getSlotType(i) == InventoryType.SlotType.RESULT) continue;
                int newAmount   = item.getAmount() + cursorAmt;
                int extraAmount = 0;
                if(newAmount > info.cursorMax)
                {
                    extraAmount = newAmount - info.cursorMax;
                    newAmount = info.cursorMax;
                }
                item.setAmount(extraAmount);
                info.topInv.setItem(i, item);
                cursorAmt = newAmount;
                if(cursorAmt == info.cursorMax)
                {
                    info.cursor.setAmount(cursorAmt);
                    return;
                }
            }
        }
        info.cursor.setAmount(cursorAmt);
    }
}
