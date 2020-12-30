package com.mikedeejay2.simplestack.system.itemclick.processes;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ProcessCollectToCursor implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        ItemStack[] topItems = info.topInv.getStorageContents();
        ItemStack[] bottomItems = info.bottomInv.getStorageContents();
        Material cursorMat = info.cursor.getType();
        int cursorAmt = info.cursorAmt;
        for(int i = 0; i < topItems.length; ++i)
        {
            ItemStack item = topItems[i];
            if(item == null) continue;
            if(item.getType() != cursorMat) continue;
            int newAmount = item.getAmount() + cursorAmt;
            int extraAmount = 0;
            if(newAmount > info.cursorMax)
            {
                extraAmount =  newAmount - info.cursorMax;
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
        for(int i = bottomItems.length - 1; i >= 0; --i)
        {
            ItemStack item = bottomItems[i];
            if(item == null) continue;
            if(item.getType() != cursorMat) continue;
            int newAmount = item.getAmount() + cursorAmt;
            int extraAmount = 0;
            if(newAmount > info.cursorMax)
            {
                extraAmount =  newAmount - info.cursorMax;
                newAmount = info.cursorMax;
            }
            item.setAmount(extraAmount);
            info.bottomInv.setItem(i, item);
            cursorAmt = newAmount;
            if(cursorAmt == info.cursorMax)
            {
                info.cursor.setAmount(cursorAmt);
                return;
            }
        }
    }
}
