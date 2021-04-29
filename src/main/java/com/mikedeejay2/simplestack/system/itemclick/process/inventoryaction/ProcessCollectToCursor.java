package com.mikedeejay2.simplestack.system.itemclick.process.inventoryaction;

import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.process.ItemClickProcess;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
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
        int invSlots = inventory.countSlots();
        for(int amount = 1; amount <= info.cursorMax; ++amount)
        {
            for(int i = 0; i < invSlots; ++i)
            {
                Inventory curInv = i >= info.topInv.getSize() ? info.bottomInv : info.topInv;
                int convertedSlot = inventory.convertSlot(i);
                ItemStack item = curInv.getItem(convertedSlot);
                if(item == null) continue;
                if(item.getType() != cursorMat) continue;
                if(item.getAmount() > amount) continue;
                if(!ItemComparison.equalsEachOther(item, info.cursor)) continue;
                switch(inventory.getSlotType(i))
                {
                    case ARMOR:
                    case RESULT:
                    case OUTSIDE:
                        continue;
                }
                int newAmount   = item.getAmount() + cursorAmt;
                int extraAmount = 0;
                if(newAmount > info.cursorMax)
                {
                    extraAmount = newAmount - info.cursorMax;
                    newAmount = info.cursorMax;
                }
                item.setAmount(extraAmount);
                inventory.setItem(i, item);
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
