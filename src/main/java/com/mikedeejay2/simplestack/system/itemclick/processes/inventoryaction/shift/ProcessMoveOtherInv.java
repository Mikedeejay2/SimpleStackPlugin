package com.mikedeejay2.simplestack.system.itemclick.processes.inventoryaction.shift;

import com.mikedeejay2.mikedeejay2lib.util.item.InventoryIdentifiers;
import com.mikedeejay2.simplestack.system.itemclick.ItemClickInfo;
import com.mikedeejay2.simplestack.system.itemclick.processes.ItemClickProcess;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ProcessMoveOtherInv implements ItemClickProcess
{
    protected ItemClickProcess backupProcess;

    public ProcessMoveOtherInv(ItemClickProcess backupProcess)
    {
        this.backupProcess = backupProcess;
    }

    @Override
    public void invoke(ItemClickInfo info)
    {
        info.player.sendMessage("Move to Other Inv");
        Inventory toInv = info.clickedBottom ? info.topInv : info.bottomInv;
        ItemStack[] toItems = toInv.getStorageContents();
        Material selectedMat = info.selected.getType();
        int selectedAmt = info.selectedAmt;
        int rawStart = info.clickedBottom ? 0 : info.topInv.getSize();
        for(int i = 0; i < toItems.length; ++i)
        {
            int convertedSlot = rawStart + i;
            Map.Entry<Boolean, Boolean> allowed = InventoryIdentifiers.applicableForSlot(convertedSlot, info.invView, selectedMat);
            if(info.clickedBottom && !allowed.getKey()) continue;
            if(InventoryIdentifiers.singletonSlot(convertedSlot, info.invView)) continue;
            InventoryType.SlotType slotType = info.invView.getSlotType(convertedSlot);
            if(slotType == InventoryType.SlotType.RESULT) continue;
            ItemStack item = toItems[i];
            if(item == null) continue;
            if(item.getType() == Material.AIR) continue;
            if(item.getType() != selectedMat) continue;
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
                    start = 9;
                }
                else
                {
                    end = 9;
                }
            }
            for(int i = start; i < end; ++i)
            {
                int convertedSlot = rawStart + i;
                Map.Entry<Boolean, Boolean> allowed = InventoryIdentifiers.applicableForSlot(convertedSlot, info.invView, selectedMat);
                if(info.clickedBottom && !allowed.getKey()) continue;
                boolean singleton = InventoryIdentifiers.singletonSlot(convertedSlot, info.invView);
                InventoryType.SlotType slotType = info.invView.getSlotType(convertedSlot);
                if(slotType == InventoryType.SlotType.RESULT) continue;
                ItemStack item = toItems[i];
                if(item != null && item.getType() != Material.AIR) continue;
                item = info.selected.clone();
                int newAmt = singleton ? 1 : selectedAmt;
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
