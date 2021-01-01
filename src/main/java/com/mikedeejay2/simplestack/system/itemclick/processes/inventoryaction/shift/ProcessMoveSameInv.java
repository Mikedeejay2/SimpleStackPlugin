package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction.shift;

import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProcessMoveSameInv implements ItemClickProcess
{
    @Override
    public void invoke(ItemClickInfo info)
    {
        info.player.sendMessage("Move to Same Inv");
        Inventory   toInv    = info.clickedBottom ? info.bottomInv : info.topInv;
        ItemStack[] toItems    = toInv.getStorageContents();
        Material    selectedMat = info.selected.getType();
        int selectedAmt = info.selectedAmt;
        for(int i = 0; i < toItems.length; ++i)
        {
            ItemStack item = toItems[i];
            if(item == null) continue;
            if(item.getType() != selectedMat) continue;
            int itemAmt = item.getAmount();
            if(itemAmt == info.selectedMax) continue;
            int newAmt = itemAmt + info.selectedAmt;
            if(newAmt > info.selectedMax)
            {
                selectedAmt = newAmt - info.selectedMax;
                newAmt = info.selectedMax;
            }
            else
            {
                selectedAmt = 0;
            }
            item.setAmount(newAmt);
            if(selectedAmt <= 0)
            {
                info.selected.setAmount(0);
                return;
            }
        }
        int start = 0;
        int end = toItems.length;
        if(info.slotType == InventoryType.SlotType.QUICKBAR)
        {
            start = 9;
        }
        else
        {
            end = 8;
        }
            for(int i = start; i < end; ++i)
            {
                ItemStack item = toItems[i];
                if(item != null) continue;
                item = info.selected.clone();
                int newAmt = selectedAmt;
                if(newAmt > info.selectedMax)
                {
                    selectedAmt = newAmt - info.selectedMax;
                    newAmt = info.selectedMax;
                }
                else
                {
                    selectedAmt -= newAmt;
                }
                item.setAmount(newAmt);
                toInv.setItem(i, item);
                if(selectedAmt <= 0)
                {
                    info.selected.setAmount(0);
                    return;
                }
            }
        info.selected.setAmount(selectedAmt);
    }
}
