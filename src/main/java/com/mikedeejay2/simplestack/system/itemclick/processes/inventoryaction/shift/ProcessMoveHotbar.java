package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction.shift;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.mikedeejay2lib.util.item.ItemComparison;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import com.mikedeejay2.simplestack.util.CheckUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProcessMoveHotbar implements ItemClickProcess
{
    protected ItemClickProcess backupProcess;

    public ProcessMoveHotbar(ItemClickProcess backupProcess)
    {
        this.backupProcess = backupProcess;
    }

    @Override
    public void invoke(ItemClickInfo info)
    {
        Inventory toInv = info.clickedBottom ? info.topInv : info.bottomInv;
        ItemStack[] toItems = toInv.getStorageContents();
        Material selectedMat = info.selected.getType();
        int selectedAmt = info.selectedAmt;
        int rawStart = info.clickedBottom ? 0 : info.topInv.getSize();
        if(InventoryIdentifiers.takeResult(info.getAction()) && info.slotType == InventoryType.SlotType.RESULT)
        {
            return;
        }
        for(int i = 0; i < toItems.length; ++i)
        {
            int convertedSlot = rawStart + i;
            InventoryType.SlotType slotType = info.invView.getSlotType(convertedSlot);
            if(slotType == InventoryType.SlotType.RESULT) continue;
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

        boolean hotbarFix = info.clickedTop;
        for(int section = 0; section < (hotbarFix ? 2 : 1); ++section)
        {
            int start = 0;
            int end   = toItems.length;
            if(hotbarFix)
            {
                if(section == 0)
                {
                    end = 9;
                }
                else
                {
                    start = 9;
                }
            }
            for(int i = start; i < end; ++i)
            {
                int convertedSlot = rawStart + i;
                InventoryType.SlotType slotType = info.invView.getSlotType(convertedSlot);
                if(slotType == InventoryType.SlotType.RESULT) continue;
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
        }
        info.selected.setAmount(selectedAmt);
        if(selectedAmt == info.selectedAmt)
        {
            backupProcess.invoke(info);
        }
    }
}
