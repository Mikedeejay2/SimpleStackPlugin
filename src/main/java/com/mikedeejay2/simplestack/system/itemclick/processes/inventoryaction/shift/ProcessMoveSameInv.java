package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction.shift;

import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
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
        Inventory toInv = info.clickedBottom ? info.bottomInv : info.topInv;
        ItemStack[] toItems = toInv.getStorageContents();
        Material selectedMat = info.selected.getType();
        int selectedAmt = info.selectedAmt;

        int start = 0;
        int end   = toItems.length;
        if(info.clickedInv.getType() == InventoryType.PLAYER)
        {
            if(info.slotType == InventoryType.SlotType.QUICKBAR)
            {
                start = 9;
            }
            else
            {
                end = 9;
            }
        }
        for(int i = start; i < end; ++i)
        {
            ItemStack item = toItems[i];
            if(item == null) continue;
            if(item.getType() == Material.AIR) continue;
            if(!ItemComparison.equalsEachOther(item, info.selected)) continue;
            int itemAmt = item.getAmount();
            if(itemAmt == info.selectedMax) continue;
            int newAmt = itemAmt + selectedAmt;
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
                info.selected.setAmount(selectedAmt);
                return;
            }
        }
        for(int i = start; i < end; ++i)
        {
            ItemStack item = toItems[i];
            if(item != null && item.getType() != Material.AIR) continue;
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
