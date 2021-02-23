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

public class ProcessMoveOffhand implements ItemClickProcess
{
    protected ItemClickProcess backupProcess;

    public ProcessMoveOffhand(ItemClickProcess backupProcess)
    {
        this.backupProcess = backupProcess;
    }

    @Override
    public void invoke(ItemClickInfo info)
    {
        info.player.sendMessage("Move to Offhand");
        Inventory toInv       = info.clickedBottom ? info.bottomInv : info.topInv;
        Material          selectedMat = info.selected.getType();
        int               selectedAmt = info.selectedAmt;
        if(InventoryIdentifiers.takeResult(info.getAction()) && info.slotType == InventoryType.SlotType.RESULT)
        {
            return;
        }

        int start;
        int end;
        if(info.slot == InventoryIdentifiers.OFFHAND_SLOT)
        {
            start = 9;
            end = toInv.getSize() - 5;
        }
        else
        {
            start = InventoryIdentifiers.OFFHAND_SLOT;
            end = InventoryIdentifiers.OFFHAND_SLOT + 1;
        }

        for(int i = start; i < end; ++i)
        {
            ItemStack item = toInv.getItem(i);
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
            ItemStack item = toInv.getItem(i);
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
        if(selectedAmt == info.selectedAmt)
        {
            backupProcess.invoke(info);
        }
    }
}
