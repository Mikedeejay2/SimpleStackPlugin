package com.mikedeejay2.simplestack.system.processes.itemclick;

import com.mikedeejay2.simplestack.Simplestack;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ProcessCollectToCursor extends ItemClickProcess
{
    public ProcessCollectToCursor(InventoryClickEvent event, Simplestack plugin)
    {
        super(event, plugin);
    }

    @Override
    public void execute()
    {
        ItemStack[]         topItems    = topInv.getStorageContents();
        ItemStack[] bottomItems = bottomInv.getStorageContents();
        Material    cursorMat   = cursor.getType();
        for(int i = 0; i < topItems.length; ++i)
        {
            ItemStack item = topItems[i];
            if(item == null) continue;
            if(item.getType() != cursorMat) continue;
            int newAmount = item.getAmount() + cursorAmt;
            int extraAmount = 0;
            if(newAmount > cursorMax)
            {
                extraAmount =  newAmount - cursorMax;
                newAmount = cursorMax;
            }
            item.setAmount(extraAmount);
            topInv.setItem(i, item);
            cursorAmt = newAmount;
            if(cursorAmt == cursorMax)
            {
                cursor.setAmount(cursorAmt);
                return;
            }
        }
        for(int i = bottomItems.length - 1; i >= 0; --i)
        {
            ItemStack item = bottomItems[i];
            if(item == null) continue;
            if(item.getType() != cursorMat) continue;
            int newAmount = item.getAmount() + cursorAmt;
            int extraAmount = 0;
            if(newAmount > cursorMax)
            {
                extraAmount =  newAmount - cursorMax;
                newAmount = cursorMax;
            }
            item.setAmount(extraAmount);
            bottomInv.setItem(i, item);
            cursorAmt = newAmount;
            if(cursorAmt == cursorMax)
            {
                cursor.setAmount(cursorAmt);
                return;
            }
        }
    }
}
